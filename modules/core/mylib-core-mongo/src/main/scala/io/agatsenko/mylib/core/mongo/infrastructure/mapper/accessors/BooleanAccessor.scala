/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonBoolean, BsonDocument}
import org.mongodb.scala.bson.BsonValue

class BooleanAccessor extends FieldAccessor[Boolean] {
  override def from(bsonValue: BsonValue): Boolean = getValue(bsonValue.asBoolean())

  override def to(value: Boolean): BsonValue = if (value) BsonBoolean.TRUE else BsonBoolean.FALSE

  override def set(doc: BsonDocument, name: String, value: Boolean): Unit = doc.put(name, to(value))

  override def get(doc: BsonDocument, name: String): Boolean = getValue(doc.getBoolean(name))

  private def getValue(bson: BsonBoolean): Boolean = bson.getValue
}
