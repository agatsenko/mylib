/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-29
  */
package io.agatsenko.mylib.infrastructure.concurrent

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object Futures {
  object extensions {
    implicit class FutureExtensions[+T](val wrapped: Future[T]) extends AnyVal {
      def syncResult(atMost: Duration): T = Await.result(wrapped, atMost)

      def syncResult: T = syncResult(Duration.Inf)
    }
  }
}
