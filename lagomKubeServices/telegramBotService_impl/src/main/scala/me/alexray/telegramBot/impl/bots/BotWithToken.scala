package me.alexray.telegramBot.impl.bots

/**
  * Created by Alexander Ray on 26.04.17.
  **/

import info.mukel.telegrambot4s.api.TelegramBot


abstract class BotWithToken(val token: String) extends TelegramBot

