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
  def from(bson: BsonValue): T

  def to(value: T): BsonValue

  def set(doc: BsonDocument, name: String, value: T): Unit

  def get(doc: BsonDocument, name: String): T
}

object FieldAccessor {
  val ID_FIELD_NAME = "_id"

  def getArray[T, C[_]](
      doc: BsonDocument,
      fieldName: String)(
      implicit accessor: FieldAccessor[T],
      cbf: CanBuildFrom[Nothing, T, C[T]]): C[T] = {
    Check.argNotNull(doc, "doc")
    val collBuilder = cbf()
    doc.getArray(fieldName).forEach(bsonValue => collBuilder += accessor.from(bsonValue))
    collBuilder.result()
  }

  def toArray[T](fieldName: String, coll: Iterable[T])(implicit accessor: FieldAccessor[T]): BsonArray = {
    // FIXME: not yet implemented
    ???
  }

  def setArray[T](
      doc: BsonDocument,
      fieldName: String,
      coll: Iterable[T])(
      implicit accessor: FieldAccessor[T]): Unit = {
    Check.argNotNull(doc, "doc")
    Check.argNotNull(coll, "coll")
    val array = new BsonArray
    coll.foreach(value => array.add(accessor.to(value)))
    doc.put(fieldName, array)
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
