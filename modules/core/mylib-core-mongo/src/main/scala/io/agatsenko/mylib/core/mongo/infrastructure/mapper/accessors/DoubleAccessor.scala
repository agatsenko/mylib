/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.BsonDocument
import org.mongodb.scala.bson.{BsonDouble, BsonValue}

class DoubleAccessor extends FieldAccessor[Double] {
  override def from(bson: BsonValue): Double = getValue(bson.asDouble())

  override def to(value: Double): BsonValue = new BsonDouble(value)

  override def set(doc: BsonDocument, name: String, value: Double): Unit = doc.put(name, to(value))

  override def get(doc: BsonDocument, name: String): Double = getValue(doc.getDouble(name))

  private def getValue(bson: BsonDouble): Double = bson.getValue
}
