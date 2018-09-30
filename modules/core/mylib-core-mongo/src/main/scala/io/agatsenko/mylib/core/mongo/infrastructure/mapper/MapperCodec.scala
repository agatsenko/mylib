/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.{BsonDocument, BsonReader, BsonWriter}
import org.bson.codecs.configuration.CodecRegistry

private[mapper] class MapperCodec[T](
    private val mapperRegistry: MapperRegistry,
    private val codecRegistry: CodecRegistry,
    private val valueType: Class[T],
    private val mapper: Mapper[T]) extends Codec[T] {
  override def getEncoderClass: Class[T] = valueType

  override def encode(writer: BsonWriter, value: T, encoderContext: EncoderContext): Unit = {
    val doc = new BsonDocument()
    mapper.writeInsert(value, newMapperContext(doc))
    getBsonDocCodec.encode(writer, doc, encoderContext)
  }

  override def decode(reader: BsonReader, decoderContext: DecoderContext): T = {
    val doc = getBsonDocCodec.decode(reader, decoderContext)
    mapper.read(newMapperContext(doc))
  }

  private def getBsonDocCodec: Codec[BsonDocument] = codecRegistry.get(classOf[BsonDocument])

  private def newMapperContext(doc: BsonDocument): MapperContext = {
    new DefaultMapperContext(doc, mapperRegistry)
  }
}
