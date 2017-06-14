package me.alexray.wolfram.impl

import akka.actor.ActorSystem
import com.lightbend.lagom.scaladsl.server._
import play.api.libs.ws.ahc.AhcWSComponents
import com.softwaremill.macwire._
import me.alexray.lagom.kube.client.LagomKubeModeComponents
import me.alexray.wolfram.api.WolframService

class WolframServiceLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new WolframServiceApplication(context) with LagomKubeModeComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new WolframServiceApplication(context) with LagomKubeModeComponents

  override def describeServices = List(
    readDescriptor[WolframService]
  )
}

abstract class WolframServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context) with AhcWSComponents
{
  implicit val system = ActorSystem("WolframServiceApplication")

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[WolframService](wire[WolframServiceImpl])
}
