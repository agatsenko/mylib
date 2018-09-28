/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import java.time.{Instant, LocalDateTime, ZoneOffset}

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDateTime, BsonDocument}

class LocalDateTimeAccessor extends FieldAccessor[LocalDateTime] {
  override def set(doc: BsonDocument, name: String, value: LocalDateTime): Unit = {
    doc.put(name, new BsonDateTime(value.toInstant(ZoneOffset.UTC).toEpochMilli))
  }

  override def get(doc: BsonDocument, name: String): LocalDateTime = {
    LocalDateTime.ofInstant(Instant.ofEpochMilli(doc.getDateTime(name).getValue), ZoneOffset.UTC)
  }
}
