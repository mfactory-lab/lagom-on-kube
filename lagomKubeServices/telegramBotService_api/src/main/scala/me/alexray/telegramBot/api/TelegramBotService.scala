package me.alexray.telegramBot.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import me.alexray.lagom.kube.api.ServiceDescriptor

trait TelegramBotService extends Service {

  val sd = ServiceDescriptor("telegram-bot", "v1")

  def hello(id: String): ServiceCall[NotUsed, String]

  override final def descriptor: Descriptor = {
    import Service._
    named(sd.name).withCalls(
      pathCall(sd.versionedPath("hello/:id"), hello _)
    ).withAutoAcl(true)
  }
}

