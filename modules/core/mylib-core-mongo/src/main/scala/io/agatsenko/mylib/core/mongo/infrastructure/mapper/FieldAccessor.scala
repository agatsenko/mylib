/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import java.time._
import java.util.UUID

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors._
import org.bson.BsonDocument
import org.bson.types.ObjectId

trait FieldAccessor[T] {
  def set(doc: BsonDocument, name: String, value: T): Unit

  def get(doc: BsonDocument, name: String): T
}

object FieldAccessor {
  val ID_FIELD_NAME = "_id"

  object Implicit {
    implicit val booleanAccessor: FieldAccessor[Boolean] = new BooleanAccessor

    implicit val byteAccessor: FieldAccessor[Byte] = new ByteAccessor

    implicit val shortAccessor: FieldAccessor[Short] = new ShortAccessor

    implicit val intAccessor: FieldAccessor[Int] = new IntAccessor

    implicit val longAccessor: FieldAccessor[Long] = new LongAccessor

    implicit val doubleAccessor: FieldAccessor[Double] = new DoubleAccessor

    implicit val bigDecimalAccessor: FieldAccessor[BigDecimal] = new BigDecimalAccessor

    implicit val stringAccessor: FieldAccessor[String] = new StringAccessor

    implicit val uuidAccessor: FieldAccessor[UUID] = new UuidAccessor

    implicit val zonedDateTimeAccessor: FieldAccessor[ZonedDateTime] = new ZonedDateTimeAccessor

    implicit val offsetDateTimeAccessor: FieldAccessor[OffsetDateTime] = new OffsetDateTimeAccessor

    implicit val localDateTimeAccessor: FieldAccessor[LocalDateTime] = new LocalDateTimeAccessor

    implicit val localDateAccessor: FieldAccessor[LocalDate] = new LocalDateAccessor

    implicit val localTimeAccessor: FieldAccessor[LocalTime] = new LocalTimeAccessor

    implicit val objectIdAccessor: FieldAccessor[ObjectId] = new ObjectIdAccessor

    implicit def optionAccessor[T](implicit accessor: FieldAccessor[T]): FieldAccessor[Option[T]] = {
      new OptionAccessor[T](accessor)
    }
  }
}
