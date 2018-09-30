/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import java.time.LocalTime

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDateTime, BsonDocument}
import org.mongodb.scala.bson.BsonValue

class LocalTimeAccessor extends FieldAccessor[LocalTime] {
  override def from(bson: BsonValue): LocalTime = getValue(bson.asDateTime())

  override def to(value: LocalTime): BsonValue = new BsonDateTime(value.toNanoOfDay)

  override def set(doc: BsonDocument, name: String, value: LocalTime): Unit = doc.put(name, to(value))

  override def get(doc: BsonDocument, name: String): LocalTime = getValue(doc.getDateTime(name))

  def getValue(bson: BsonDateTime): LocalTime = LocalTime.ofNanoOfDay(bson.getValue)
}
