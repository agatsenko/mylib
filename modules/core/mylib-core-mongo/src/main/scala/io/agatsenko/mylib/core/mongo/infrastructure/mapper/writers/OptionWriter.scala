/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.writers

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldWriter
import org.bson.{BsonDocument, BsonNull}

class OptionWriter[T](val valueWriter: FieldWriter[T]) extends FieldWriter[Option[T]] {
  override def write(doc: BsonDocument, name: String, opt: Option[T]): Unit = {
    opt match {
      case Some(value) => valueWriter.write(doc, name, value)
      case None => doc.put(name, BsonNull.VALUE)
    }
  }
}
