/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.BsonDocument
import org.mongodb.scala.bson.{BsonInt64, BsonValue}

class LongAccessor extends FieldAccessor[Long] {
  override def from(bson: BsonValue): Long = getValue(bson.asInt64())

  override def to(value: Long): BsonValue = new BsonInt64(value)

  override def set(doc: BsonDocument, name: String, value: Long): Unit = doc.put(name, to(value))

  override def get(doc: BsonDocument, name: String): Long = getValue(doc.getInt64(name))

  def getValue(bson: BsonInt64): Long = bson.getValue
}
