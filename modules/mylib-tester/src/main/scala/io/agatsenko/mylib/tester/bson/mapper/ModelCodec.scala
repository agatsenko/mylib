/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-03-09
 */
package io.agatsenko.mylib.tester.bson.mapper

import io.mango.common.util.Check
import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.{BsonReader, BsonWriter}
import org.bson.codecs.configuration.CodecRegistry

class ModelCodec[T](val mapInfo: MapInfo[T], codecRegistry: CodecRegistry) extends Codec[T] {
  Check.argNotNull(mapInfo, "mapInfo")

  override def getEncoderClass: Class[T] = mapInfo.modelType

  override def decode(reader: BsonReader, decoderContext: DecoderContext): T = {
    // FIXME: not implemented
    ???
  }

  override def encode(writer: BsonWriter, value: T, encoderContext: EncoderContext): Unit = {
    // FIXME: not implemented
    ???
  }
}
