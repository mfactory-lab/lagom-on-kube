package me.alexray.telegramBot.impl

import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import me.alexray.telegramBot.api.TelegramBotService
import com.softwaremill.macwire._
import me.alexray.lagom.kube.client.LagomKubeModeComponents
import me.alexray.wolfram.api.WolframService

class TelegramBotServiceLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new TelegramBotServiceApplication(context) with LagomKubeModeComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new TelegramBotServiceApplication(context) with LagomKubeModeComponents

  override def describeServices = List(
    readDescriptor[TelegramBotService]
  )
}

abstract class TelegramBotServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context) with AhcWSComponents
{
  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[TelegramBotService](wire[TelegramBotServiceImpl])

  // Bind the TwbService client
  lazy val wolframService: WolframService = serviceClient.implement[WolframService]
}
