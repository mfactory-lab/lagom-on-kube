package me.alexray.wolfram.impl

import java.net.URLEncoder

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.ServiceCall
import me.alexray.wolfram.api.WolframService
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}


class WolframServiceImpl(config: Configuration)
                        (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends WolframService
{

  val appID = config.underlying.getString("wolfram.appid")
  val apiUrl = s"http://api.wolframalpha.com/v2/"


  override def query(q: String): ServiceCall[NotUsed, String] = ServiceCall { _ =>

    val url = apiUrl + s"query?appid=$appID&input=" + URLEncoder.encode(q, "UTF-8")

    for {
      response <- Http().singleRequest(HttpRequest(uri = Uri(url)))
      if response.status.isSuccess()
      data <- Unmarshal(response).to[String]
    } yield data

  }

  override def simple(q: String): ServiceCall[NotUsed, Array[Byte]] = ServiceCall { _ =>

    println(s"quetions = '$q'")

    val url = apiUrl + s"simple?appid=$appID&input=" +  URLEncoder.encode(q, "UTF-8").replace("+", "%20")

    println(s"url = '$url'")

    for {
      response <- Http().singleRequest(HttpRequest(uri = Uri(url)))
      if response.status.isSuccess()
      bytes <- Unmarshal(response).to[ByteString]
    } yield {
      println(s"received image ${bytes.size} bytes long")
      bytes.toArray
    }

  }
}
