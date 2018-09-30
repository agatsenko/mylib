/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import scala.collection.mutable
import scala.reflect.{classTag, ClassTag}

import io.mango.common.util.Check
import org.bson.codecs.configuration.{CodecProvider, CodecRegistries, CodecRegistry}
import org.mongodb.scala.bson.BsonDocument

trait MapperRegistry {
  def isRegistered(valueType: Class[_]): Boolean

  def isRegistered[T: ClassTag]: Boolean = isRegistered(classTag[T].runtimeClass)

  def getMapper[T](valueType: Class[T]): Mapper[T]

  def getMapper[T: ClassTag]: Mapper[T] = getMapper(classTag[T].runtimeClass.asInstanceOf[Class[T]])

  def createInsertDocument[T](valueType: Class[T], value: T): BsonDocument

  def createInsertDocument[T](value: T): BsonDocument = createDocument(value, createInsertDocument[T])

  def createUpdateDocument[T](valueType: Class[T], value: T): BsonDocument

  def createUpdateDocument[T](value: T): BsonDocument = createDocument(value, createUpdateDocument[T])

  def buildCodecProviders: Seq[CodecProvider]

  def buildCodecRegistry: CodecRegistry = CodecRegistries.fromProviders(buildCodecProviders: _*)

  private def createDocument[T](valueType: Class[T], value: T, create: (Class[T], T) => BsonDocument): BsonDocument = {
    create(valueType, value)
  }

  private def createDocument[T](value: T, create: (Class[T], T) => BsonDocument): BsonDocument = {
    Check.argNotNull(value, "value")
    createDocument(value.getClass.asInstanceOf[Class[T]], value, create)
  }
}

object MapperRegistry {
  def newBuilder: Builder = new Builder

  class Builder private[MapperRegistry]() {
    private val mappers = mutable.Map[Class[_], Mapper[_]]()

    def contains(valueType: Class[_]): Boolean = mappers.contains(valueType)

    def contains[T: ClassTag]: Boolean = contains(classTag[T].runtimeClass)

    def add[T](valueType: Class[T], mapper: Mapper[T]): this.type = {
      Check.argNotNull(valueType, "valueType")
      Check.argNotNull(mapper, "mapper")
      mappers.put(valueType, mapper)
      this
    }

    def add[T: ClassTag](mapper: Mapper[T]): this.type = {
      add(classTag[T].runtimeClass.asInstanceOf[Class[T]], mapper)
    }

    def remove(valueType: Class[_]): this.type = {
      mappers.remove(valueType)
      this
    }

    def remove[T: ClassTag](): this.type = {
      remove(classTag[T].runtimeClass)
      this
    }

    def build(): MapperRegistry = new DefaultMapperRegistry(mappers.toMap)
  }
}