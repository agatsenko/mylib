/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-29
  */
package io.agatsenko.mylib.infrastructure.concurrent

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.control.NonFatal

object FutureExt {
  def successful[T](f: => T): Future[T] = Future.successful(f)

  def sync[T](f: => T): Future[T] = {
    try {
      Future.successful(f)
    }
    catch {
      case NonFatal(ex) => Future.failed(ex)
    }
  }

  implicit class FutureExt[+T](val wrapped: Future[T]) extends AnyVal {
    def syncResult(atMost: Duration): T = Await.result(wrapped, atMost)

    def syncResult: T = syncResult(Duration.Inf)
  }
}
