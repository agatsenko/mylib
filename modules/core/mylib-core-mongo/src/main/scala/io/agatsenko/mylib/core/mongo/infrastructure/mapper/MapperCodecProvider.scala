/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import org.bson.codecs.configuration.{CodecProvider, CodecRegistry}
import org.bson.codecs.Codec

private[mapper] class MapperCodecProvider(private val mapperRegistry: MapperRegistry) extends CodecProvider {
  override def get[T](clazz: Class[T], codecRegistry: CodecRegistry): Codec[T] = {
    new MapperCodec[T](mapperRegistry, codecRegistry, clazz, mapperRegistry.getMapper(clazz))
  }
}
