/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import io.mango.common.util.SimpleValExt.IntExt
import org.bson.BsonDocument
import org.mongodb.scala.bson.{BsonInt32, BsonValue}

class ByteAccessor extends FieldAccessor[Byte] {
  override def toValue(bson: BsonValue): Byte = getValue(bson.asInt32())

  override def toBson(value: Byte): BsonValue = new BsonInt32(value)

  override def set(doc: BsonDocument, name: String, value: Byte): Unit = doc.put(name, toBson(value))

  override def get(doc: BsonDocument, name: String): Byte = getValue(doc.getInt32(name))

  private def getValue(bson: BsonInt32): Byte = bson.asInt32().getValue.toByteExact
}
