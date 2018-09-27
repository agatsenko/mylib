/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import io.mango.common.util.Check
import org.bson.{BsonDocument, BsonNull}

private[mapper] class DefaultMapperContext(
    val document: BsonDocument,
    private val mapperRegistry: MapperRegistry) extends MapperContext {
  override def getMapper[T](valueType: Class[T]): Mapper[T] = mapperRegistry.getMapper(valueType)

  override def write[T](fieldName: String, valueType: Class[T], value: T): Unit = {
    Check.argNotNullOrEmpty(fieldName, "fieldName")
    Check.argNotNull(valueType, "valueType")
    Check.argNotNull(value, "value")
    if (value == null) {
      document.put(fieldName, null)
    }
    else {
      val valueDocument = new BsonDocument()
      val mapper = getMapper(valueType)
      mapper.write(value, new DefaultMapperContext(valueDocument, mapperRegistry))
      document.put(fieldName, valueDocument)
    }
  }

  override def writeOpt[T](fieldName: String, valueType: Class[T], valueOpt: Option[T]): Unit = {
    Check.argNotNullOrEmpty(fieldName, "fieldName")
    Check.argNotNull(valueType, "valueType")
    Check.argNotNull(valueOpt, "valueOpt")
    valueOpt match {
      case None => document.put(fieldName, BsonNull.VALUE)
      case Some(value) => write(fieldName, valueType, value)
    }
  }

  override def read[T](fieldName: String, valueType: Class[T]): T = {
    Check.argNotNullOrEmpty(fieldName, "fieldName")
    Check.argNotNull(valueType, "valueType")
    getMapper(valueType).read(new DefaultMapperContext(document.getDocument(fieldName), mapperRegistry))
  }

  override def readOpt[T](fieldName: String, valueType: Class[T]): Option[T] = {
    Check.argNotNullOrEmpty(fieldName, "fieldName")
    Check.argNotNull(valueType, "valueType")
    if (!document.containsKey(fieldName) || document.isNull(fieldName)) None else Some(read(fieldName, valueType))
  }
}
