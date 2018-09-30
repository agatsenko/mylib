/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDocument, BsonNull}
import org.mongodb.scala.bson.BsonValue

class OptionAccessor[T](val accessor: FieldAccessor[T]) extends FieldAccessor[Option[T]] {
  override def from(bson: BsonValue): Option[T] = if (bson.isNull) None else Some(accessor.from(bson))

  override def to(opt: Option[T]): BsonValue = {
    opt match {
      case Some(value) => accessor.to(value)
      case None => BsonNull.VALUE
    }
  }

  override def set(doc: BsonDocument, name: String, opt: Option[T]): Unit = doc.put(name, to(opt))

  override def get(doc: BsonDocument, name: String): Option[T] = {
    if (doc.containsKey(name)) from(doc.get(name)) else None
  }
}
