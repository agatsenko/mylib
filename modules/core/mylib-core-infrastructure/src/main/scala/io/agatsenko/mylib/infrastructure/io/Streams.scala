/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-17
  */
package io.agatsenko.mylib.infrastructure.io

import java.io.{BufferedInputStream, BufferedOutputStream, InputStream, OutputStream}

object Streams {
  def toBufferedStream(in: InputStream, buffSize: Option[Int]): InputStream = {
    in match {
      case _: BufferedInputStream => in
      case _ => buffSize.map(new BufferedInputStream(in, _)).getOrElse(new BufferedInputStream(in))
    }
  }

  def toBufferedStream(in: InputStream): InputStream = toBufferedStream(in, None)

  def toBufferedStream(out: OutputStream, buffSize: Option[Int]): OutputStream = {
    out match {
      case _: BufferedOutputStream => out
      case _ => buffSize.map(new BufferedOutputStream(out, _)).getOrElse(new BufferedOutputStream(out))
    }
  }

  def toBufferedStream(out: OutputStream): OutputStream = toBufferedStream(out, None)

  def copy(in: InputStream, out: OutputStream): Unit = {
    val buffIn = toBufferedStream(in)
    val buffOut = toBufferedStream(out)
    var b = -1
    while ({b = buffIn.read(); b > -1}) {
      buffOut.write(b)
    }
    buffOut.flush()
  }
}
