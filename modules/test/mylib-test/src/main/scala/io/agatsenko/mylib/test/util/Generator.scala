/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-17
  */
package io.agatsenko.mylib.test.util

import scala.util.Random

import java.util.UUID

object Generator {
  def newUuidStr: String = UUID.randomUUID().toString.replace("-", "")

  def newStr(length: Int = 10): String = {
    val sb = StringBuilder.newBuilder
    for (_ <- 1 to length) {
      sb.append(Random.nextInt(255).toChar)
    }
    sb.toString()
  }
}
