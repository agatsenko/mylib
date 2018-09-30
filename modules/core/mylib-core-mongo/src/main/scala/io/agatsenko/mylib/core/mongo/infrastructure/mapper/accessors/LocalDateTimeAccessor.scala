/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import java.time.{Instant, LocalDateTime, ZoneOffset}

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDateTime, BsonDocument}
import org.mongodb.scala.bson.BsonValue

class LocalDateTimeAccessor extends FieldAccessor[LocalDateTime] {
  override def toValue(bson: BsonValue): LocalDateTime = getValue(bson.asDateTime())

  override def toBson(value: LocalDateTime): BsonValue = new BsonDateTime(value.toInstant(ZoneOffset.UTC).toEpochMilli)

  override def set(doc: BsonDocument, name: String, value: LocalDateTime): Unit = doc.put(name, toBson(value))

  override def get(doc: BsonDocument, name: String): LocalDateTime = getValue(doc.getDateTime(name))

  def getValue(bson: BsonDateTime): LocalDateTime = {
    LocalDateTime.ofInstant(Instant.ofEpochMilli(bson.getValue), ZoneOffset.UTC)
  }
}
