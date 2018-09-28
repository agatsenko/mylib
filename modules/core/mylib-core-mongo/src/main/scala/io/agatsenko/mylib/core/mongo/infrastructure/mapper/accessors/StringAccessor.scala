/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.BsonDocument
import org.mongodb.scala.bson.BsonString

class StringAccessor extends FieldAccessor[String] {
  override def set(doc: BsonDocument, name: String, value: String): Unit = doc.put(name, new BsonString(value))

  override def get(doc: BsonDocument, name: String): String = doc.getString(name).getValue
}
