/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.BsonValueCodecProvider

private[mapper] class DefaultMapperRegistry (private val mappers: Map[Class[_], Mapper[_]]) extends MapperRegistry{
  private val bsonCodecProvider = new BsonValueCodecProvider()

  override def isRegistered(valueType: Class[_]): Boolean = mappers.contains(valueType)

  override def getMapper[T](valueType: Class[T]): Mapper[T] = mappers(valueType).asInstanceOf[Mapper[T]]

  override def buildCodecProviders: Seq[CodecProvider] = Seq(bsonCodecProvider, new MapperCodecProvider(this))
}
