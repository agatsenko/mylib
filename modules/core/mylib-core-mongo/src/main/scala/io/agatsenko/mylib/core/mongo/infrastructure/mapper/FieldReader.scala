/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-27
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import java.util.UUID

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.readers._
import org.bson.BsonDocument

trait FieldReader[T] {
  def read(doc: BsonDocument, name: String): T
}

object FieldReader {
  trait ImplicitReaders {
    implicit val booleanReader: FieldReader[Boolean] = new BooleanReader

    implicit val byteReader: FieldReader[Byte] = new ByteReader

    implicit val shortReader: FieldReader[Short] = new ShortReader

    implicit val intReader: FieldReader[Int] = new IntReader

    implicit val longReader: FieldReader[Long] = new LongReader

    implicit val doubleReader: FieldReader[Double] = new DoubleReader

    implicit val bigDecimalReader: FieldReader[BigDecimal] = new BigDecimalReader

    implicit val stringReader: FieldReader[String] = new StringReader

    implicit val uuidReader: FieldReader[UUID] = new UuidReader

    implicit def optionReader[T](implicit reader: FieldReader[T]): FieldReader[Option[T]] = new OptionReader[T](reader)
  }

  object Implicit extends ImplicitReaders
}
