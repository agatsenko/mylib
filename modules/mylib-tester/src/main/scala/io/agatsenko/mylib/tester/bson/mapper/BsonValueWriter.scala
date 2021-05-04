/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-03-09
 */
package io.agatsenko.mylib.tester.bson.mapper

import org.bson.BsonWriter

trait BsonValueWriter[T] extends ((BsonWriter, T) => Unit) {
}

object BsonValueWriter {
  def write[T](bsonWriter: BsonWriter, value: T)(implicit writer: BsonValueWriter[T]): Unit = writer(bsonWriter, value)
}

object BsonValueWriters {
  implicit val charWriter: BsonValueWriter[Char] = (bsonWriter, value) => bsonWriter.writeString(value.toString)

  implicit val intWriter: BsonValueWriter[Int] = (bsonWriter, value) => bsonWriter.writeInt32(value)
}
