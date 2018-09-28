/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import java.time.LocalDate

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDateTime, BsonDocument}

class LocalDateAccessor extends FieldAccessor[LocalDate] {
  override def set(doc: BsonDocument, name: String, value: LocalDate): Unit = {
    doc.put(name, new BsonDateTime(value.toEpochDay))
  }

  override def get(doc: BsonDocument, name: String): LocalDate = LocalDate.ofEpochDay(doc.getDateTime(name).getValue)
}
