/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import scala.language.higherKinds

import scala.collection.generic.CanBuildFrom

import java.time._
import java.util.UUID

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors._
import io.mango.common.util.Check
import org.bson.{BsonDocument, BsonNull}
import org.bson.types.ObjectId
import org.mongodb.scala.bson.{BsonArray, BsonValue}

trait FieldAccessor[T] {
  def toValue(bson: BsonValue): T

  def toBson(value: T): BsonValue

  def set(doc: BsonDocument, name: String, value: T): Unit

  def get(doc: BsonDocument, name: String): T
}

object FieldAccessor {
  val ID_FIELD_NAME = "_id"

  def toValue[T](bson: BsonValue)(implicit accessor: FieldAccessor[T]): T = accessor.toValue(bson)

  def toBson[T](value: T)(implicit accessor: FieldAccessor[T]): BsonValue = {
    if (value == null) BsonNull.VALUE else accessor.toBson(value)
  }

  def toValue[T, C[_]](
      array: BsonArray)(
      implicit accessor: FieldAccessor[T],
      cbf: CanBuildFrom[Nothing, T, C[T]]): C[T] = {
    val collBuilder = cbf()
    if (array != null) {
      array.forEach(bson => collBuilder += accessor.toValue(bson))
    }
    collBuilder.result()
  }

  def toBson[T](values: Iterable[T])(implicit accessor: FieldAccessor[T]): BsonArray = {
    val array = new BsonArray
    if (values != null) {
      values.foreach(v => array.add(accessor.toBson(v)))
    }
    array
  }

  def getArray[T, C[_]](
      doc: BsonDocument,
      fieldName: String)(
      implicit accessor: FieldAccessor[T],
      cbf: CanBuildFrom[Nothing, T, C[T]]): C[T] = {
    Check.argNotNull(doc, "doc")
    toValue(doc.getArray(fieldName))
  }

  def setArray[T](
      doc: BsonDocument,
      fieldName: String,
      values: Iterable[T])(
      implicit accessor: FieldAccessor[T]): Unit = {
    Check.argNotNull(doc, "doc")
    doc.put(fieldName, toBson(values))
  }

  def setNull(doc: BsonDocument, fieldName: String): Unit = {
    Check.argNotNull(doc, "doc")
    doc.put(fieldName, BsonNull.VALUE)
  }

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
