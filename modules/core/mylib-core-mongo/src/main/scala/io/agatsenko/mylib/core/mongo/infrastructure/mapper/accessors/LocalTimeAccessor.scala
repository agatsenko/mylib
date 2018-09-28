/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import java.time.LocalTime

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDateTime, BsonDocument}

class LocalTimeAccessor extends FieldAccessor[LocalTime] {
  override def set(doc: BsonDocument, name: String, value: LocalTime): Unit = {
    doc.put(name, new BsonDateTime(value.toNanoOfDay))
  }

  override def get(doc: BsonDocument, name: String): LocalTime = LocalTime.ofNanoOfDay(doc.getDateTime(name).getValue)
}
