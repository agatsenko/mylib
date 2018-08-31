/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-08-31
  */
package com.agat.mylib.infrastructure

import scala.util.{Failure, Success, Try}

package object util {
  def using[TResource <: AutoCloseable, TResult](resource: TResource)(action: TResource => TResult): TResult =
    Using.using(resource)(action)

  implicit class TryOps[T](val tr: Try[T]) extends AnyVal {
    def eventually[TIgnore](action: Try[T] => TIgnore): Try[T] = Try(action(tr)) match {
      case Failure(ex) => tr match {
        case Success(_) => Failure(ex)
        case Failure(srcEx) =>
          ex.addSuppressed(srcEx)
          Failure(ex)
      }
      case _ => tr
    }
  }
}
