/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.writers

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldWriter
import org.bson.{BsonDecimal128, BsonDocument}
import org.bson.types.Decimal128

class BigDecimalWriter extends FieldWriter[BigDecimal] {
  override def write(doc: BsonDocument, name: String, value: BigDecimal): Unit = {
    doc.put(name, new BsonDecimal128(new Decimal128(value.bigDecimal)))
  }
}
