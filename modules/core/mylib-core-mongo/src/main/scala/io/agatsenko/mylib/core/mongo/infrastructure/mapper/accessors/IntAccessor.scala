/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDocument, BsonInt32}

class IntAccessor extends FieldAccessor[Int] {
  override def set(doc: BsonDocument, name: String, value: Int): Unit = doc.put(name, new BsonInt32(value))

  override def get(doc: BsonDocument, name: String): Int = doc.getInt32(name).getValue
}
