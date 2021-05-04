/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-03-08
 */
package io.agatsenko.mylib.tester.bson

import scala.util.Random

import java.util.UUID

case class Doc(intVal: Int, strVal: String, ints: Seq[Int], doc: Option[Doc]) {
}

object Doc {
  def generate(): Doc = Doc(
    Random.nextInt(),
    UUID.randomUUID().toString,
    Seq(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt()),
    Some(
      Doc(
        Random.nextInt(),
        UUID.randomUUID().toString,
        Seq(Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt(), Random.nextInt()),
        None
      )
    )
  )
}