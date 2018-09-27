/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.readers

import java.util.UUID

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldReader
import org.bson.BsonDocument

class UuidReader extends FieldReader[UUID] {
  override def read(doc: BsonDocument, name: String): UUID = {
    val uuidBytes = doc.getBinary(name).getData

    var lsb = 0L
    for (i <- 8 to 15) {
      lsb <<= 8;
      lsb |= uuidBytes(i) & 0xFFL;
    }

    var msb = 0L
    for (i <- 0 to 7) {
      msb <<= 8
      msb |= uuidBytes(i) & 0xFFL;
    }

    new UUID(msb, lsb)
  }
}
