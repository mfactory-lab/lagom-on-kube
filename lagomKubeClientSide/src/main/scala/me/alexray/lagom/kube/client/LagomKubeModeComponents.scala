package me.alexray.lagom.kube.client

import com.lightbend.lagom.scaladsl.devmode.LagomDevModeServiceLocatorComponents
import play.api.inject.ApplicationLifecycle

trait LagomKubeModeComponents extends LagomDevModeServiceLocatorComponents {
  def applicationLifecycle: ApplicationLifecycle

  // Eagerly register services
  new KubeServiceRegistration(serviceInfo, applicationLifecycle, configuration, serviceRegistry, actorSystem)(executionContext)
}
