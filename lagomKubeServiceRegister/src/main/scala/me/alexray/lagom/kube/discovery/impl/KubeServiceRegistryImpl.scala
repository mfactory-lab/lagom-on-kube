/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 *
 * based on https://github.com/lagom/lagom/blob/1.3.3/dev/service-registry/service-locator/src/main/java/com/lightbend/lagom/discovery/impl/ServiceRegistryImpl.java
 *
 */

package me.alexray.lagom.kube.discovery.impl

import java.net.URI
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Named}

import akka.NotUsed
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import com.lightbend.lagom.internal.javadsl.registry.{RegisteredService, ServiceRegistry, ServiceRegistryService}
import com.lightbend.lagom.javadsl.api.ServiceCall
import com.lightbend.lagom.javadsl.api.transport.NotFound
import org.pcollections.PSequence
import play.api.Logger

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import me.alexray.lagom.kube.discovery.KubeServiceRegistryActor

import scala.language.implicitConversions


class KubeServiceRegistryImpl @Inject() (@Named(KubeServiceRegistryModule.KUBE_SERVICE_REGISTRY_ACTOR) registry: ActorRef)
  extends ServiceRegistry
{
  import me.alexray.lagom.converters.ServiceCallConverter._

  private val logger: Logger = Logger(this.getClass)
  implicit val timeout = Timeout(Duration.create(5, TimeUnit.SECONDS))
  import scala.concurrent.ExecutionContext.Implicits.global

  override def register(name: String): ServiceCall[ServiceRegistryService, NotUsed] = (service: ServiceRegistryService) =>
  {
    logger.debug("register invoked, name=[" + name + "], request=[" + service + "]")
    (registry ? KubeServiceRegistryActor.Register(name, service)).map(_ => NotUsed)
  }

  override def unregister(name: String): ServiceCall[NotUsed, NotUsed] = (request: NotUsed) => {
    logger.debug("unregister invoked, name=[" + name + "], request=[" + request + "]")

    registry ! KubeServiceRegistryActor.Remove(name)

    Future.successful(NotUsed)
  }

  override def lookup(name: String): ServiceCall[NotUsed, URI] = (request: NotUsed) => {
    logger.debug("locate invoked, name=[" + name + "], request=[" + request + "]")

    (registry ? KubeServiceRegistryActor.Lookup(name)).mapTo[Option[URI]].map {
      case Some(uri) =>
        logger.debug("Location of service name=[" + name + "] is " + uri)
        uri
      case None =>
        logger.debug("Service name=[" + name + "] has not been registered")
        throw new NotFound(name)
    }
  }

  override def registeredServices(): ServiceCall[NotUsed, PSequence[RegisteredService]] = (request: NotUsed) => {
    (registry ? KubeServiceRegistryActor.GetRegisteredServices).mapTo[KubeServiceRegistryActor.RegisteredServices].map(_.services)
  }

}
