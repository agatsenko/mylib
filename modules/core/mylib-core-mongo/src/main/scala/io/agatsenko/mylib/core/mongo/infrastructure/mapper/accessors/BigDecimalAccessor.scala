/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDecimal128, BsonDocument}
import org.bson.types.Decimal128

class BigDecimalAccessor extends FieldAccessor[BigDecimal] {
  override def set(doc: BsonDocument, name: String, value: BigDecimal): Unit = {
    doc.put(name, new BsonDecimal128(new Decimal128(value.bigDecimal)))
  }

  override def get(doc: BsonDocument, name: String): BigDecimal = {
    doc.getDecimal128(name).decimal128Value().bigDecimalValue()
  }
}
