/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.BsonDocument
import org.mongodb.scala.bson.{BsonString, BsonValue}

class StringAccessor extends FieldAccessor[String] {
  override def from(bson: BsonValue): String = getValue(bson.asString())

  override def to(value: String): BsonValue = new BsonString(value)

  override def set(doc: BsonDocument, name: String, value: String): Unit = doc.put(name, to(value))

  override def get(doc: BsonDocument, name: String): String = getValue(doc.getString(name))

  private def getValue(bson: BsonString): String = bson.getValue
}
