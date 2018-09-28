/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-17
  */
package io.agatsenko.mylib.infrastructure

import java.time._

// FIXME: need to remove
object Tester {
  def main(args: Array[String]): Unit = {
    val srcdt = OffsetDateTime.now()
    val epochMillis = srcdt.toInstant.toEpochMilli
    val dt = OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
    println(srcdt)
    println(dt)
  }
}
