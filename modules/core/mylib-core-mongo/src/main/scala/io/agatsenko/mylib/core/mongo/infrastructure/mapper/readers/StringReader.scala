/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.readers

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldReader
import org.bson.BsonDocument

class StringReader extends FieldReader[String] {
  override def read(doc: BsonDocument, name: String): String = doc.getString(name).getValue
}
