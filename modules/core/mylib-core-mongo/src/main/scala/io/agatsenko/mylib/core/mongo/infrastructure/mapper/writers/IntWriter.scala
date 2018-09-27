/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.writers

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldWriter
import org.bson.{BsonDocument, BsonInt32}

class IntWriter extends FieldWriter[Int] {
  override def write(doc: BsonDocument, name: String, value: Int): Unit = doc.put(name, new BsonInt32(value))
}
