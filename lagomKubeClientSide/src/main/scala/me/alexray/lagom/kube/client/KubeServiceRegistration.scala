/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */

package me.alexray.lagom.kube.client

import java.net.URI

import akka.actor.ActorSystem
import com.lightbend.lagom.internal.scaladsl.registry.{ServiceRegistry, ServiceRegistryService}
import com.lightbend.lagom.scaladsl.api.ServiceInfo
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Logger}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class KubeServiceRegistration(serviceInfo: ServiceInfo,
                              lifecycle: ApplicationLifecycle,
                              config: Configuration,
                              registry: ServiceRegistry,
                              actorSystem: ActorSystem)(implicit ec: ExecutionContext)
{

  private val logger: Logger = Logger(this.getClass)
  private val uri = {
    val httpAddress =
      config.getString("service.http.address")
        .getOrElse(config.underlying.getString("play.server.http.address"))
    val httpPort = config.getString("play.server.http.port").get
    val uri = s"http://$httpAddress:$httpPort"
    logger.info(s"uri: $uri")
    URI.create(uri)
  }

  def completeServiceName(name: String): String =
    name + "-" + config.getString("service.instance.suffix").getOrElse("default").hashCode


  lifecycle.addStopHook { () =>
    Future.sequence(serviceInfo.locatableServices.map {
      case (service, _) =>
        registry.unregister(completeServiceName(service)).invoke()
    }).map(_ => ())
  }


  private def heartBeat(): Unit = {
    actorSystem.scheduler.schedule(1 seconds, 1 minutes) {
      logger.debug("register service heartbeat ")
      register()
    }
  }

  private def register(): Unit = {

    serviceInfo.locatableServices.foreach {
      case (service, acls) =>
        registry.register(completeServiceName(service))
          .invoke(ServiceRegistryService(uri, acls))
          .onComplete {
            case Success(_) =>
              logger.info(s"Service name=[$service] successfully registered with service locator.")
            case Failure(e) =>
              logger.error(s"Service name=[$service] couldn't register itself to the service locator.", e)
              logger.info("Service will try to register in 10 seconds with next heartbeat event")
          }
    }

  }

  heartBeat()

}
