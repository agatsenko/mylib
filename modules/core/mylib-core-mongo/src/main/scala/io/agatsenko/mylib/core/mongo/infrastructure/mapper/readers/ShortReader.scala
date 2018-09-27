/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.readers

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldReader
import io.mango.common.util.SimpleValExt.IntExt
import org.bson.BsonDocument

class ShortReader extends FieldReader[Short] {
  override def read(doc: BsonDocument, name: String): Short = doc.getInt32(name).getValue.toShortExact
}
