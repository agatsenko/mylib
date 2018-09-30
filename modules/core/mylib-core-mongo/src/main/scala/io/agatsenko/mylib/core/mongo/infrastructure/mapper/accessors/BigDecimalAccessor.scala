/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.{BsonDecimal128, BsonDocument}
import org.bson.types.Decimal128
import org.mongodb.scala.bson.BsonValue

class BigDecimalAccessor extends FieldAccessor[BigDecimal] {
  override def toValue(bson: BsonValue): BigDecimal = getValue(bson.asDecimal128())

  override def toBson(value: BigDecimal): BsonValue = new BsonDecimal128(new Decimal128(value.bigDecimal))

  override def set(doc: BsonDocument, name: String, value: BigDecimal): Unit = doc.put(name, toBson(value))

  override def get(doc: BsonDocument, name: String): BigDecimal = getValue(doc.getDecimal128(name))

  private def getValue(bson: BsonDecimal128): BigDecimal = bson.getValue.bigDecimalValue()
}
