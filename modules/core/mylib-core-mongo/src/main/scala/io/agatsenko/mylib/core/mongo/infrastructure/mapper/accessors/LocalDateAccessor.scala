/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import java.time.LocalDate

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDateTime, BsonDocument}
import org.mongodb.scala.bson.BsonValue

class LocalDateAccessor extends FieldAccessor[LocalDate] {
  override def from(bson: BsonValue): LocalDate = getValue(bson.asDateTime())

  override def to(value: LocalDate): BsonValue = new BsonDateTime(value.toEpochDay)

  override def set(doc: BsonDocument, name: String, value: LocalDate): Unit = doc.put(name, to(value))

  override def get(doc: BsonDocument, name: String): LocalDate = getValue(doc.getDateTime(name))

  private def getValue(bson: BsonDateTime): LocalDate = LocalDate.ofEpochDay(bson.getValue)
}
