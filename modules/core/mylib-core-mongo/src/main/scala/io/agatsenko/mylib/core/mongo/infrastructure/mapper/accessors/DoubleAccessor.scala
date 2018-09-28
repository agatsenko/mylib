/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.BsonDocument
import org.mongodb.scala.bson.BsonDouble

class DoubleAccessor extends FieldAccessor[Double] {
  override def set(doc: BsonDocument, name: String, value: Double): Unit = doc.put(name, new BsonDouble(value))

  override def get(doc: BsonDocument, name: String): Double = doc.getDouble(name).getValue
}
