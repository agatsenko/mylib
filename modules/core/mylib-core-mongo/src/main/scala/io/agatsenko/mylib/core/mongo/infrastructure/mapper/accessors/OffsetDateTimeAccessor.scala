/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import java.time.{Instant, OffsetDateTime, ZoneId}

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDateTime, BsonDocument}

class OffsetDateTimeAccessor extends FieldAccessor[OffsetDateTime] {
  private val systemZoneId = ZoneId.systemDefault()

  override def set(doc: BsonDocument, name: String, value: OffsetDateTime): Unit = {
    doc.put(name, new BsonDateTime(value.toInstant.toEpochMilli))
  }

  override def get(doc: BsonDocument, name: String): OffsetDateTime = {
    OffsetDateTime.ofInstant(Instant.ofEpochMilli(doc.getDateTime(name).getValue), systemZoneId)
  }
}
