/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 *
 * based on https://github.com/lagom/lagom/blob/1.3.3/dev/service-registry/service-locator/src/main/scala/com/lightbend/lagom/discovery/ServiceRegistryActor.scala
 *
 */

package me.alexray.lagom.kube.discovery

import java.net.{InetSocketAddress, URI}
import java.util.regex.Pattern

import akka.Done
import akka.actor.{Actor, ActorLogging, Status}
import akka.pattern.{Backoff, BackoffSupervisor, ask, pipe}
import akka.util.Timeout
import com.google.inject.Inject
import com.lightbend.lagom.discovery.{ServiceLocatorServer, UnmanagedServices}
import com.lightbend.lagom.internal.javadsl.registry.{RegisteredService, ServiceRegistryService}
import com.lightbend.lagom.javadsl.api.transport.{TransportErrorCode, TransportException}
import me.alexray.lagom.kube.discovery.storage.{KubePersistActor, RegisterState, ServiceInstances, ServiceName}
import org.pcollections.{PSequence, TreePVector}
import play.api.Logger

import scala.concurrent.duration._
import scala.collection.JavaConverters._
import scala.collection.immutable
import scala.compat.java8.OptionConverters._

object KubeServiceRegistryActor {
  case class Lookup(name: String)
  case class Remove(name: String)
  case class Register(name: String, service: ServiceRegistryService)
  case class Route(method: String, path: String)
  case object GetRegisteredServices
  case class RegisteredServices(services: PSequence[RegisteredService])
  sealed trait RouteResult
  case class Found(address: InetSocketAddress) extends RouteResult
  case class NotFound(registry: Map[String, ServiceRegistryService]) extends RouteResult
}

class KubeServiceRegistryActor @Inject()(unmanagedServices: UnmanagedServices)
  extends Actor with ActorLogging
{
  override def preStart: Unit =
    context.system.eventStream.subscribe(self, classOf[RegisterState])

  log.debug("KubeServiceRegistryActor created")

  import KubeServiceRegistryActor._

  private var router = PartialFunction.empty[Route, InetSocketAddress]
  private var routerFunctions = Seq.empty[PartialFunction[Route, InetSocketAddress]]

  private val supervisorProps = BackoffSupervisor.props(
    Backoff.onStop(
      KubePersistActor.props(unmanagedServices = unmanagedServices),
      childName = "KubePersistActor",
      minBackoff = 3.seconds,
      maxBackoff = 30.seconds,
      randomFactor = 0.2))

  private val persist = context.actorOf(supervisorProps, name = "SupervisorForKubePersistActor")

  implicit private val timeout = Timeout(5.seconds)
  implicit private val ec = context.dispatcher

  override def receive: Receive = {

    case state: RegisterState =>
      log.debug(s"received new state: $state")
      rebuildRouter(state)

    case lookup: Lookup =>
      (persist ? lookup).pipeTo(sender)

    case Remove(name) =>
      persist ! KubePersistActor.RemoveServiceCommand(name)

    case Register(name, service) =>
      persist ! KubePersistActor.RegisterServiceCommand(name, service)
      sender() ! Done


    case GetRegisteredServices =>
      (persist ? GetRegisteredServices).mapTo[RegisterState]
        .map { state =>
          val services: List[RegisteredService] = state.registry.map {
            case (serviceName, instances) =>
              RegisteredService.of(serviceName.name, instances.srs.uri())
          }.toList

          import scala.collection.JavaConverters._
          RegisteredServices(TreePVector.from(services.asJava))

        } pipeTo sender

    case route: Route =>
      sender() ! router.lift(route).fold[RouteResult](NotFound(Map.empty))(Found.apply)
      warnOnAmbiguity(route)
  }

  private def warnOnAmbiguity(route: Route) = {
    if (log.isWarningEnabled) {
      val servingRoutes = routerFunctions.filter(_.isDefinedAt(route))
      if (servingRoutes.size > 1) {
        val servers = servingRoutes.map(_.apply(route))
        log.warning(s"Ambiguous route resolution serving route: $route. Route served by ${servers.head} but also matches ${servers.tail.mkString("[", ",", "]")}.")
      }
    }
  }

  private def serviceRouter(service: ServiceRegistryService) = {

    // lazy because if there's no ACLs, then there's no need to create an InetSocketAddress, and hence no need to fail
    // if the port can't be calculated.
    //lazy val address = (addressUri.getScheme, addressUri.getHost, addressUri.getPort) match {
    def address() = {
      val addressUri: URI = new URI(service.uri.toString)

      log.debug(s"(addressUri, addressUri.getScheme, addressUri.getHost, addressUri.getPort): ($addressUri, ${addressUri.getScheme}, ${addressUri.getHost}, ${addressUri.getPort})")

      val res = (addressUri.getScheme, addressUri.getHost, addressUri.getPort) match {
        case (_, null, _)        => throw new IllegalArgumentException("Cannot register a URI that doesn't have a host: " + addressUri)
        case ("http", host, -1)  => new InetSocketAddress(host, 80)
        case ("https", host, -1) => new InetSocketAddress(host, 443)
        case (_, _, -1)          => throw new IllegalArgumentException("Cannot register a URI that does not specify a port: " + addressUri)
        case (_, host, port)     => {
          log.debug(s"requested new socket address for host: $host and port: $port")
          val sa = new InetSocketAddress(host, port)
          log.debug(s"sd: $sa")
          sa
        }
      }

      log.debug(s"adress: $res")
      res
    }

    val routerFunctions: Seq[PartialFunction[Route, InetSocketAddress]] = service.acls.asScala
      .map { acl =>
        acl.method().asScala -> acl.pathRegex().asScala.map(Pattern.compile)
      }
      .map {
        case (aclMethod, pathRegex) =>
          val pf: PartialFunction[Route, InetSocketAddress] = {
            case Route(method, path) if aclMethod.forall(_.name == method) && pathRegex.forall(_.matcher(path).matches()) =>
              log.debug(s"Route(method, path): ($method:$path)")

              address()
          }
          pf
      }

    routerFunctions
  }

  private def rebuildRouter(state: RegisterState) = {
    routerFunctions = state.registry.map(x => x._1 -> x._2.srs).values.flatMap(serviceRouter).toSeq
    router = routerFunctions.foldLeft(PartialFunction.empty[Route, InetSocketAddress])(_ orElse _)
  }

}

private class ServiceAlreadyRegistered(serviceName: String) extends TransportException(
  TransportErrorCode.PolicyViolation, s"A service with the same name=[$serviceName] was already registered"
) {
}
