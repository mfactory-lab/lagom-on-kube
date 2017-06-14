package me.alexray.telegramBot.impl.bots

import info.mukel.telegrambot4s.api.{ChatActions, Commands, Polling}
import info.mukel.telegrambot4s.methods.SendPhoto
import info.mukel.telegrambot4s.models.InputFile
import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.methods._
import info.mukel.telegrambot4s.models._
import me.alexray.wolfram.api.WolframService

class WolframBot(token: String, service: WolframService)
  extends BotWithToken(token)
    with Polling with Commands with ChatActions
{

  on("/ask") { implicit message => args =>
    reply("думаю, надо подождать")
    val x = service.simple(args mkString " ").invoke()

    x.failed.foreach{ t =>
      println(t.getLocalizedMessage)
      reply("этот вопрос идиотский. давай другой")
    }

    x.foreach { (data: Array[Byte]) =>
      println(s"received image ${data.length} bytes long")
      val photo: InputFile = InputFile("qrcode.png", data)
      reply(s"received image ${data.length} bytes long")
      uploadingPhoto // Hint the user
      request(SendPhoto(message.source, photo))
    }
  }

  on("/test") { implicit message => args =>

    reply("it works!")

  }

  on("/q") { implicit message => args =>
    service.query(args mkString " ").invoke().foreach { data =>
      reply(data.take(100))
    }
  }
}

object WolframBot {
  def apply(token: String, service: WolframService) = new WolframBot(token, service)
}