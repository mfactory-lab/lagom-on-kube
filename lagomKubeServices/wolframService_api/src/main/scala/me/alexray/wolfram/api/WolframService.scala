package me.alexray.wolfram.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import me.alexray.lagom.kube.api.ServiceDescriptor

trait WolframService extends Service {

  val sd = ServiceDescriptor("wolfram", "v1")

  def query(q: String): ServiceCall[NotUsed, String]
  def simple(q: String): ServiceCall[NotUsed, Array[Byte]]

  override final def descriptor: Descriptor = {
    import Service._

    named(sd.name)
      .withCalls(
        pathCall(sd.versionedPath("query/:q"), query _),
        pathCall(sd.versionedPath("simple/:q"), simple _)
      ).withAutoAcl(true)
  }
}

