/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.writers

import java.util.UUID

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldWriter
import org.bson.{BsonBinary, BsonBinarySubType, BsonDocument}

class UuidWriter extends FieldWriter[UUID] {
  override def write(doc: BsonDocument, name: String, value: UUID): Unit = {
    val uuidBytes = new Array[Byte](16);

    var lsb = value.getLeastSignificantBits
    for (i <- 15 to 8 by -1) {
      uuidBytes(i) = (lsb & 0xFFL).toByte
      lsb >>= 8
    }

    var msb = value.getMostSignificantBits
    for (i <- 7 to 0 by -1) {
      uuidBytes(i) = (msb & 0xFFL).toByte
      msb >>= 8
    }

    doc.put(name, new BsonBinary(BsonBinarySubType.UUID_STANDARD, uuidBytes))
  }
}
