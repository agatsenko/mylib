/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import java.time.{Instant, OffsetDateTime, ZoneId}

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDateTime, BsonDocument}
import org.mongodb.scala.bson.BsonValue

class OffsetDateTimeAccessor extends FieldAccessor[OffsetDateTime] {
  private val systemZoneId = ZoneId.systemDefault()

  override def from(bson: BsonValue): OffsetDateTime = getValue(bson.asDateTime())

  override def to(value: OffsetDateTime): BsonValue = new BsonDateTime(value.toInstant.toEpochMilli)

  override def set(doc: BsonDocument, name: String, value: OffsetDateTime): Unit = doc.put(name, to(value))

  override def get(doc: BsonDocument, name: String): OffsetDateTime = getValue(doc.getDateTime(name))

  private def getValue(bson: BsonDateTime): OffsetDateTime = {
    OffsetDateTime.ofInstant(Instant.ofEpochMilli(bson.getValue), systemZoneId)
  }
}
