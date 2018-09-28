/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import java.util.UUID

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonBinary, BsonBinarySubType, BsonDocument}

class UuidAccessor extends FieldAccessor[UUID] {
  override def set(doc: BsonDocument, name: String, value: UUID): Unit = {
    val uuidBytes = new Array[Byte](16)

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

  override def get(doc: BsonDocument, name: String): UUID = {
    val uuidBytes = doc.getBinary(name).getData

    var lsb = 0L
    for (i <- 8 to 15) {
      lsb <<= 8
      lsb |= uuidBytes(i) & 0xFFL
    }

    var msb = 0L
    for (i <- 0 to 7) {
      msb <<= 8
      msb |= uuidBytes(i) & 0xFFL
    }

    new UUID(msb, lsb)
  }
}
