/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import io.mango.common.util.Check
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.BsonValueCodecProvider
import org.mongodb.scala.bson.BsonDocument

private[mapper] class DefaultMapperRegistry(private val mappers: Map[Class[_], Mapper[_]]) extends MapperRegistry {
  private val bsonCodecProvider = new BsonValueCodecProvider()

  override def isRegistered(valueType: Class[_]): Boolean = mappers.contains(valueType)

  override def getMapper[T](valueType: Class[T]): Mapper[T] = mappers(valueType).asInstanceOf[Mapper[T]]

  override def createInsertDocument[T](valueType: Class[T], value: T): BsonDocument = {
    createDocument(valueType, value) { (mapper, value, context) =>
      mapper.writeInsert(value, context)
    }
  }

  override def createUpdateDocument[T](valueType: Class[T], value: T): BsonDocument = {
    createDocument(valueType, value) { (mapper, value, context) =>
      mapper.writeUpdate(value, context)
    }
  }

  override def buildCodecProviders: Seq[CodecProvider] = Seq(bsonCodecProvider, new MapperCodecProvider(this))

  private def createDocument[T](
      valueType: Class[T],
      value: T)(
      writeDoc: (Mapper[T], T, MapperContext) => Unit): BsonDocument = {
    Check.argNotNull(valueType, "valueType")
    Check.argNotNull(value, "value")
    val doc = new BsonDocument
    writeDoc(getMapper(valueType), value, new DefaultMapperContext(doc, this))
    doc
  }
}
