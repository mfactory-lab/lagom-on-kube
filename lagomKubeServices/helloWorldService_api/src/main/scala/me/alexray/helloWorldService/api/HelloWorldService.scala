package me.alexray.helloWorldService.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import me.alexray.lagom.kube.api.ServiceDescriptor

trait HelloWorldService extends Service {

  val sd = ServiceDescriptor("helloworldservice", "v1")

  def hello(): ServiceCall[NotUsed, List[String]]

  override final def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named(sd.name).withCalls(
      pathCall(sd.versionedPath("hello"), hello _)
    ).withAutoAcl(true)
    // @formatter:on
  }
}

