/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDocument, BsonNull}

class OptionAccessor[T](val accessor: FieldAccessor[T]) extends FieldAccessor[Option[T]] {
  override def set(doc: BsonDocument, name: String, opt: Option[T]): Unit = {
    opt match {
      case Some(value) => accessor.set(doc, name, value)
      case None => doc.put(name, BsonNull.VALUE)
    }
  }

  override def get(doc: BsonDocument, name: String): Option[T] = {
    if (!doc.containsKey(name) || doc.isNull(name)) None else Some(accessor.get(doc, name))
  }
}
