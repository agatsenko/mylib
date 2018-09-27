/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.writers

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldWriter
import org.bson.BsonDocument
import org.mongodb.scala.bson.BsonInt64

class LongWriter extends FieldWriter[Long] {
  override def write(doc: BsonDocument, name: String, value: Long): Unit = doc.put(name, new BsonInt64(value))
}
