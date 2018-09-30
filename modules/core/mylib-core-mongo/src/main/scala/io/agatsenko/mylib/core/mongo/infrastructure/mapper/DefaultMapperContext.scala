/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import scala.language.higherKinds

import scala.collection.generic.CanBuildFrom

import io.mango.common.util.Check
import org.bson.BsonDocument
import org.mongodb.scala.bson.BsonArray

private[mapper] class DefaultMapperContext(
    val document: BsonDocument,
    private val mapperRegistry: MapperRegistry) extends MapperContext {
  override def getMapper[T](valueType: Class[T]): Mapper[T] = mapperRegistry.getMapper(valueType)

  override def writeAsDocument[T](fieldName: String, valueType: Class[T], value: T): Unit = {
    document.put(fieldName, createDocument(getMapper(valueType), value))
  }

  override def writeArrayAsDocument[T](fieldName: String, valueType: Class[T], values: Iterable[T]): Unit = {
    Check.argNotNull(values, "values")
    val array = new BsonArray
    val mapper = getMapper(valueType)
    values.foreach(v => array.add(createDocument(mapper, v)))
    document.put(fieldName, array)
  }

  override def readFromDocument[T](fieldName: String, valueType: Class[T]): T = {
    Check.argNotNullOrEmpty(fieldName, "fieldName")
    Check.argNotNull(valueType, "valueType")
    getMapper(valueType).read(new DefaultMapperContext(document.getDocument(fieldName), mapperRegistry))
  }

  override def readArrayFromDocument[T, C[_]](
      fieldName: String,
      valueType: Class[T])(
      implicit cbf: CanBuildFrom[Nothing, T, C[T]]): C[T] = {
    val array = document.getArray(fieldName)
    val mapper = getMapper(valueType)
    val collBuilder = cbf()
    array.forEach { bsonValue =>
      collBuilder += mapper.read(new DefaultMapperContext(bsonValue.asDocument(), mapperRegistry))
    }
    collBuilder.result()
  }

  private def createDocument[T](mapper: Mapper[T], value: T): BsonDocument = {
    Check.argNotNull(value, "value")
    val doc = new BsonDocument()
    mapper.writeInsert(value, new DefaultMapperContext(doc, mapperRegistry))
    doc
  }
}
