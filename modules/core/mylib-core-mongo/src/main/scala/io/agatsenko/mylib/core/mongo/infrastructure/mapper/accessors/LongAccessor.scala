/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.BsonDocument
import org.mongodb.scala.bson.BsonInt64

class LongAccessor extends FieldAccessor[Long] {
  override def set(doc: BsonDocument, name: String, value: Long): Unit = doc.put(name, new BsonInt64(value))

  override def get(doc: BsonDocument, name: String): Long = doc.getInt64(name).getValue
}
