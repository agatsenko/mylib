/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import java.time.{Instant, ZonedDateTime, ZoneId}

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDateTime, BsonDocument}

class ZonedDateTimeAccessor extends FieldAccessor[ZonedDateTime] {
  private val systemZoneId = ZoneId.systemDefault()

  override def set(doc: BsonDocument, name: String, value: ZonedDateTime): Unit = {
    doc.put(name, new BsonDateTime(value.toInstant.toEpochMilli))
  }

  override def get(doc: BsonDocument, name: String): ZonedDateTime = {
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(doc.getDateTime(name).getValue), systemZoneId)
  }
}
