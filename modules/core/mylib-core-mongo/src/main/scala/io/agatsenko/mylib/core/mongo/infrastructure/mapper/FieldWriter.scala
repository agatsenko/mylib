/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-27
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import java.util.UUID

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.writers._
import org.bson.BsonDocument

trait FieldWriter[T] {
  def write(doc: BsonDocument, name: String, value: T): Unit
}

object FieldWriter {
  trait ImplicitWriters {
    implicit val booleanWriter: FieldWriter[Boolean] = new BooleanWriter

    implicit val byteWriter: FieldWriter[Byte] = new ByteWriter

    implicit val shortWriter: FieldWriter[Short] = new ShortWriter

    implicit val intWriter: FieldWriter[Int] = new IntWriter

    implicit val longWriter: FieldWriter[Long] = new LongWriter

    implicit val doubleWriter: FieldWriter[Double] = new DoubleWriter

    implicit val bigDecimalWriter: FieldWriter[BigDecimal] = new BigDecimalWriter

    implicit val stringWriter: FieldWriter[String] = new StringWriter

    implicit val uuidWriter: FieldWriter[UUID] = new UuidWriter

    implicit def optionWriter[T](implicit writer: FieldWriter[T]): FieldWriter[Option[T]] = new OptionWriter[T](writer)
  }

  object Implicit extends ImplicitWriters
}
