/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDocument, BsonInt32}
import org.mongodb.scala.bson.BsonValue

class IntAccessor extends FieldAccessor[Int] {
  override def from(bson: BsonValue): Int = getValue(bson.asInt32())

  override def to(value: Int): BsonValue = new BsonInt32(value)

  override def set(doc: BsonDocument, name: String, value: Int): Unit = doc.put(name, to(value))

  override def get(doc: BsonDocument, name: String): Int = getValue(doc.getInt32(name))

  private def getValue(bson: BsonInt32): Int = bson.getValue
}
