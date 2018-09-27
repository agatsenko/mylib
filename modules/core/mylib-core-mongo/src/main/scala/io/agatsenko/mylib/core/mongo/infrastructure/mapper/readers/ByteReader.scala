/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.readers

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldReader
import io.mango.common.util.SimpleValExt.IntExt
import org.bson.BsonDocument

class ByteReader extends FieldReader[Byte] {
  override def read(doc: BsonDocument, name: String): Byte = doc.getInt32(name).getValue.toByteExact
}
