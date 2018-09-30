/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-30
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import scala.language.higherKinds

import scala.collection.generic.CanBuildFrom

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.BsonDocument
import org.mongodb.scala.bson.{BsonArray, BsonValue}

class ArrayAccessor[T, C[_] <: Iterable[T]](
    val accessor: FieldAccessor[T],
    val cbf: CanBuildFrom[Nothing, T, C[T]]) extends FieldAccessor[C[T]] {
  override def from(bson: BsonValue): C[T] = getValue(bson.asArray())

  override def to(value: C[T]): BsonValue = {
    val array = new BsonArray
    value.foreach(v => array.add(accessor.to(v)))
    array
  }

  override def set(doc: BsonDocument, name: String, value: C[T]): Unit = doc.put(name, to(value))

  override def get(doc: BsonDocument, name: String): C[T] = getValue(doc.getArray(name))

  def getValue(bson: BsonArray): C[T] = {
    val cb = cbf()
    bson.forEach(bv => cb += accessor.from(bv))
    cb.result()
  }
}
