/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import io.mango.common.util.SimpleValExt.IntExt
import org.bson.BsonDocument
import org.mongodb.scala.bson.BsonInt32

class ShortAccessor extends FieldAccessor[Short] {
  override def set(doc: BsonDocument, name: String, value: Short): Unit = doc.put(name, new BsonInt32(value))

  override def get(doc: BsonDocument, name: String): Short = doc.getInt32(name).getValue.toShortExact
}
