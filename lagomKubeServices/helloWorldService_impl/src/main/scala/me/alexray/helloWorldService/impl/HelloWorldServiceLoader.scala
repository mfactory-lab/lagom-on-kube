package me.alexray.helloWorldService.impl

import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire.wire
import me.alexray.lagom.kube.client.LagomKubeModeComponents
import me.alexray.helloWorldService.api.HelloWorldService
import play.api.libs.ws.ahc.AhcWSComponents

class HelloWorldServiceLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new HelloWorldServiceApplication(context) with LagomKubeModeComponents

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new HelloWorldServiceApplication(context) with LagomKubeModeComponents

  override def describeServices = List(
    readDescriptor[HelloWorldService]
  )
}

abstract class HelloWorldServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context) with AhcWSComponents
{
  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[HelloWorldService](wire[HelloWorldServiceImpl])

}
