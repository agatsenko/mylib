/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonBoolean, BsonDocument}

class BooleanAccessor extends FieldAccessor[Boolean] {
  override def set(doc: BsonDocument, name: String, value: Boolean): Unit = {
    doc.put(name, if (value) BsonBoolean.TRUE else BsonBoolean.FALSE)
  }

  override def get(doc: BsonDocument, name: String): Boolean = doc.getBoolean(name).getValue
}
