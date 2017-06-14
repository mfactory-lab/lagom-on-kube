package me.alexray.helloWorldService.impl

import java.util
import java.util.Properties

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import me.alexray.helloWorldService.api.HelloWorldService
import scala.collection.JavaConverters._

import scala.concurrent.Future

class HelloWorldServiceImpl extends HelloWorldService {

  override def hello(): ServiceCall[NotUsed, List[String]] = ServiceCall { _ =>
    Future.successful(System.getenv().asScala.toList.map{case (k, v) => s"$k->$v"})
  }
}
