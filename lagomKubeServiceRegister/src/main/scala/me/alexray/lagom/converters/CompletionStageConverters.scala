/*
*
* Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
*
* based on https://github.com/Fabszn/lagom-scala-helloworld/blob/master/helloworld-impl/src/main/scala/converter/CompletionStageConverters.scala
*
*/

package me.alexray.lagom.converters

import java.util.concurrent.CompletionStage

import scala.compat.java8.FutureConverters.CompletionStageOps
import scala.compat.java8.FutureConverters.FutureOps
import scala.concurrent.Future
import akka.NotUsed
import scala.concurrent.ExecutionContext.Implicits.global

import scala.language.implicitConversions

trait CompletionStageConverters {

  implicit def asCompletionStage[A](f: Future[A]): CompletionStage[A] = f.toJava
  implicit def asFuture[A](f: CompletionStage[A]): Future[A] = f.toScala

  implicit def asUnusedCompletionStage(f: CompletionStage[_]): CompletionStage[NotUsed] =
    f.map(_ => NotUsed.getInstance()).toJava
}

object CompletionStageConverters extends CompletionStageConverters