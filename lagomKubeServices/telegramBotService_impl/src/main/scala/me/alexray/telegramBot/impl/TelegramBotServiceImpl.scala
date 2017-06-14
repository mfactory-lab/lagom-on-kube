package me.alexray.telegramBot.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import me.alexray.telegramBot.api.TelegramBotService

import scala.concurrent.Future
import me.alexray.telegramBot.impl.bots._
import me.alexray.wolfram.api.WolframService
import play.api.Configuration

/**
  * Implementation of the TwbService.
  */
class TelegramBotServiceImpl(service: WolframService,  config: Configuration) extends TelegramBotService {

  val botToken = config.underlying.getString("telegram.bot.token")
  println(s"$botToken")

  val bot = me.alexray.telegramBot.impl.bots.WolframBot(botToken, service)

  bot.run()

  override def hello(id: String) = ServiceCall { _ =>

    Future.successful(id)
  }

}
