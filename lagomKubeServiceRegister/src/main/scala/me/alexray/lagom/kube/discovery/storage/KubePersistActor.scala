/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

package me.alexray.lagom.kube.discovery.storage

import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import com.lightbend.lagom.discovery.UnmanagedServices
import com.lightbend.lagom.internal.javadsl.registry.ServiceRegistryService
import me.alexray.lagom.kube.discovery.KubeServiceRegistryActor
import org.joda.time.DateTime

import scala.concurrent.duration._

object KubePersistActor {

  def props(unmanagedServices: UnmanagedServices) = Props(new KubePersistActor(unmanagedServices))


  // Commands
  case class RegisterServiceCommand(serviceDescription: ServiceDescription) extends Command
  object RegisterServiceCommand {
    def apply(name: String, service: ServiceRegistryService): RegisterServiceCommand =
      RegisterServiceCommand(ServiceDescription(name, service))
  }

  case class RemoveServiceCommand(serviceName: ServiceName, serviceInstance: ServiceInstanceName) extends Command
  object RemoveServiceCommand {
    def apply(name: String): RemoveServiceCommand = {
      val t = ServiceDescription.toNameInstanceTuple(name)
      RemoveServiceCommand(t._1, t._2)
    }
  }

  case object CleanUpDeadServicesCommand extends Command

  // Events
  case class ServiceRegisteredEvent(serviceDescription: ServiceDescription) extends Event
  case class ServiceRemovedEvent(serviceName: ServiceName, serviceInstance: ServiceInstanceName) extends Event
  case class DeadServicesCleanedEvent(millis: Long) extends Event

  // Messages
  case object TakeSnapshot

}

class KubePersistActor(unmanagedServices: UnmanagedServices)
  extends PersistentActor with ActorLogging
{

  log.debug(s"created KubePersistActor with unmanagedServices: $unmanagedServices")

  import KubePersistActor._

  implicit private val ec = context.dispatcher

  context.system.scheduler.schedule(10.minutes, 10.minutes)(self ! TakeSnapshot)
  context.system.scheduler.schedule(1.minute, 100.seconds)(self ! CleanUpDeadServicesCommand)

  override def persistenceId: String = "kube-persist-actor-10"

  private var state = RegisterState(unmanagedServices)

  private def updateState(newState: RegisterState) = {
    state = newState
  }

  override def receiveRecover: Receive = {
    case event @ ServiceRegisteredEvent(serviceDescription) =>
      updateState(state.addNewOrUpdateHeartBeat(serviceDescription))
      if (recoveryFinished)
        context.system.eventStream.publish(state)

    case event @ ServiceRemovedEvent(serviceName, serviceInstance) =>
      updateState(state.remove(serviceName, serviceInstance))
      if (recoveryFinished)
        context.system.eventStream.publish(state)

    case DeadServicesCleanedEvent(millis) =>
      updateState(state.remove(state.getDeadServices(new DateTime(millis), 1.minute)))
      if (recoveryFinished)
        context.system.eventStream.publish(state)

    case SnapshotOffer(_, snapshot: RegisterState) =>
      updateState(snapshot)

    case RecoveryCompleted =>
      log.info(s"recovery completed. current state is $state")
      context.system.eventStream.publish(state)

    case event =>
      log.warning(s"unknown recovery event: $event")
  }

  override def receiveCommand: Receive = {
    case RegisterServiceCommand(description) =>
      persist(ServiceRegisteredEvent(description))(receiveRecover)

    case RemoveServiceCommand(serviceName, serviceInstance) =>
      persist(ServiceRemovedEvent(serviceName, serviceInstance))(receiveRecover)

    case CleanUpDeadServicesCommand =>
      log.info(s"CleanUpDeadServicesCommand: $state")
      persist(DeadServicesCleanedEvent(DateTime.now().getMillis))(receiveRecover)

    case TakeSnapshot =>
      saveSnapshot(state)
      log.info(s"takeSnapshot: $state")

    case KubeServiceRegistryActor.Lookup(name) =>
      sender ! state.get(name).map(_.srs.uri())

    case KubeServiceRegistryActor.GetRegisteredServices =>
      sender ! state

    case msg =>
      log.warning(s"unknown command received $msg")

  }

}
