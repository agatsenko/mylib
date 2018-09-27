/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.readers

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldReader
import org.bson.BsonDocument

class OptionReader[T](val valueReader: FieldReader[T]) extends FieldReader[Option[T]] {
  override def read(doc: BsonDocument, name: String): Option[T] = {
    if (!doc.containsKey(name) || doc.isNull(name)) None else Some(valueReader.read(doc, name))
  }
}
