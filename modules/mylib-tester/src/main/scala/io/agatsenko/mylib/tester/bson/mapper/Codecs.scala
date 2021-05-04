/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-03-09
 */
package io.agatsenko.mylib.tester.bson.mapper

import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.{BsonReader, BsonWriter}

object Codecs {
  import BsonValueReaders._
  import BsonValueWriters._

  val charCodec: Codec[Char] = new Codec[Char] {
    override def getEncoderClass: Class[Char] = classOf[Char]

    override def encode(writer: BsonWriter, value: Char, encoderContext: EncoderContext): Unit =
      BsonValueWriter.write(writer, value)

    override def decode(reader: BsonReader, decoderContext: DecoderContext): Char = BsonValueReader.read[Char](reader)
  }

  val intCodec: Codec[Int] = new Codec[Int] {
    override def getEncoderClass: Class[Int] = classOf[Int]

    override def encode(writer: BsonWriter, value: Int, encoderContext: EncoderContext): Unit =
      BsonValueWriter.write(writer, value)

    override def decode(reader: BsonReader, decoderContext: DecoderContext): Int = BsonValueReader.read[Int](reader)
  }
}
