/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 *
 * based on https://github.com/lagom/lagom/blob/1.3.3/dev/service-registry/service-locator/src/main/java/com/lightbend/lagom/discovery/impl/ServiceRegistryModule.java
 *
 */

package me.alexray.lagom.kube.discovery.impl

import java.util.{Map => JMap}

import com.google.inject.AbstractModule
import com.lightbend.lagom.discovery.impl.ServiceRegistryImpl
import com.lightbend.lagom.discovery.UnmanagedServices
import com.lightbend.lagom.gateway.ServiceGatewayConfig
import com.lightbend.lagom.internal.javadsl.registry.{NoServiceLocator, ServiceRegistry}
import com.lightbend.lagom.javadsl.api.ServiceLocator
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport
import me.alexray.lagom.kube.discovery.KubeServiceRegistryActor
import me.alexray.lagom.kube.gateway.KubeServiceGatewayConfig
import play.libs.akka.AkkaGuiceSupport


object KubeServiceRegistryModule {
  final val KUBE_SERVICE_REGISTRY_ACTOR = "kubeServiceRegistryActor"
}

case class KubeServiceRegistryModule(serviceGatewayConfig: KubeServiceGatewayConfig,
                                     unmanagedServices: JMap[String, String])
  extends AbstractModule with ServiceGuiceSupport with AkkaGuiceSupport
{

  println("created kubeServiceRegistryActor")

  import KubeServiceRegistryModule._

  override protected def configure(): Unit = {
    bindService(classOf[ServiceRegistry], classOf[KubeServiceRegistryImpl])
    bindActor(classOf[KubeServiceRegistryActor], KUBE_SERVICE_REGISTRY_ACTOR)
    bind(classOf[KubeServiceGatewayConfig]).toInstance(serviceGatewayConfig)
    bind(classOf[UnmanagedServices]).toInstance(UnmanagedServices.apply(unmanagedServices))
    bind(classOf[ServiceLocator]).to(classOf[NoServiceLocator])
  }
}