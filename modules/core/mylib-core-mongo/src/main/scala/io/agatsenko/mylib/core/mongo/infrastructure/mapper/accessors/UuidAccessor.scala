/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import java.util.UUID

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonBinary, BsonBinarySubType, BsonDocument}
import org.mongodb.scala.bson.BsonValue

class UuidAccessor extends FieldAccessor[UUID] {
  override def from(bson: BsonValue): UUID = getValue(bson.asBinary())

  override def to(value: UUID): BsonValue = {
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

    new BsonBinary(BsonBinarySubType.UUID_STANDARD, uuidBytes)
  }

  override def set(doc: BsonDocument, name: String, value: UUID): Unit = doc.put(name, to(value))

  override def get(doc: BsonDocument, name: String): UUID = getValue(doc.getBinary(name))

  private def getValue(bson: BsonBinary): UUID = {
    val uuidBytes = bson.getData

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
