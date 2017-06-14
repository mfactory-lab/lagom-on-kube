/*
*
* Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
*
* based on https://github.com/Fabszn/lagom-scala-helloworld/blob/master/helloworld-impl/src/main/scala/converter/CompletionStageConverters.scala
*
*/

package me.alexray.lagom.converters

import java.util.concurrent.CompletionStage

import com.lightbend.lagom.javadsl.api.ServiceCall

import scala.concurrent.Future
import scala.language.implicitConversions

object ServiceCallConverter extends CompletionStageConverters {


  def liftToServiceCallFromStage[Request, Response](f: Request => CompletionStage[Response]): ServiceCall[Request,Response] =
    new ServiceCall[Request,Response] {
      def invoke(request: Request): CompletionStage[Response] = f(request)
    }

  implicit def liftToServiceCall[Request, Response](f: Request => Future[Response]): ServiceCall[Request,Response] =
    new ServiceCall[Request,Response] {
      def invoke(request: Request): CompletionStage[Response] = f(request)
    }

}