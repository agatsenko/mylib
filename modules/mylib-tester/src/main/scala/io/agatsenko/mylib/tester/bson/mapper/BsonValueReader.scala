/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-03-09
 */
package io.agatsenko.mylib.tester.bson.mapper

import scala.language.higherKinds

import scala.collection.mutable

import org.bson.{BsonReader, BsonType}
import org.bson.codecs.{Decoder, DecoderContext}

trait BsonValueReader[T] extends (BsonReader => T)

object BsonValueReader {
  def read[T](bsonReader: BsonReader)(implicit reader: BsonValueReader[T]): T = reader(bsonReader)
}

object BsonValueReaders {
  implicit val charReader: BsonValueReader[Char] = bsonReader => bsonReader.readString().charAt(0)

  implicit val byteReader: BsonValueReader[Byte] = bsonReader => bsonReader.readInt32().byteValue()

  implicit val shortReader: BsonValueReader[Short] = bsonReader => bsonReader.readInt32().shortValue()

  implicit val intReader: BsonValueReader[Int] = bsonReader => bsonReader.readInt32()

  implicit val longReader: BsonValueReader[Long] = bsonReader => bsonReader.readInt64()

  implicit val floatReader: BsonValueReader[Float] = bsonReader => bsonReader.readDouble().floatValue()

  implicit val doubleReader: BsonValueReader[Double] = bsonReader => bsonReader.readDouble()

  implicit val stringReader: BsonValueReader[String] = bsonReader => bsonReader.readString()

  implicit def opnReader[T](implicit reader: BsonValueReader[T]): BsonValueReader[Option[T]] =
    bsonReader => Option(reader(bsonReader))

  implicit def collectionReader[T, C[_]](
      itemDecoder: Decoder[T], decoderContext: DecoderContext)(
      implicit cb: mutable.Builder[T, C[T]]): BsonValueReader[C[T]] = bsonReader => {
    bsonReader.readStartArray()
    while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      cb += itemDecoder.decode(bsonReader, decoderContext)
    }
    bsonReader.readEndArray()
    cb.result()
  }
}
