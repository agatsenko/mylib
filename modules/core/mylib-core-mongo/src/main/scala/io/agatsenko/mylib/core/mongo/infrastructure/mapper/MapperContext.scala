/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-27
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import scala.language.higherKinds

import scala.collection.generic.CanBuildFrom
import scala.reflect.{classTag, ClassTag}

import org.bson.BsonDocument

trait MapperContext {
  def document: BsonDocument

  def getMapper[T](valueType: Class[T]): Mapper[T]

  def getMapper[T: ClassTag]: Mapper[T] = getMapper(classTag[T].runtimeClass.asInstanceOf[Class[T]])

  def set[T](filedName: String, value: T)(implicit accessor: FieldAccessor[T]): Unit = {
    accessor.set(document, filedName, value)
  }

  def setId[T](value: T)(implicit accessor: FieldAccessor[T]): Unit = set(FieldAccessor.ID_FIELD_NAME, value)

  def setArray[T](fieldName: String, coll: Iterable[T])(implicit accessor: FieldAccessor[T]): Unit = {
    FieldAccessor.setArray(document, fieldName, coll)
  }

  def writeAsDocument[T](fieldName: String, valueType: Class[T], value: T): Unit

  def writeArrayAsDocument[T](fieldName: String, valueType: Class[T], values: Iterable[T]): Unit

  def writeOptAsDocument[T](fieldName: String, valueType: Class[T], valueOpt: Option[T]): Unit = {
    valueOpt match {
      case None => FieldAccessor.setNull(document, fieldName)
      case Some(value) => writeAsDocument(fieldName, valueType, value)
    }
  }

  def writeAsDocument[T: ClassTag](fieldName: String, value: T): Unit = {
    writeAsDocument(fieldName, classTag[T].runtimeClass.asInstanceOf[Class[T]], value)
  }

  def writeAsDocument[T: ClassTag](fieldName: String, values: Iterable[T]): Unit = {
    writeArrayAsDocument(fieldName, classTag[T].runtimeClass.asInstanceOf[Class[T]], values)
  }

  def writeOptAsDocument[T: ClassTag](fieldName: String, value: Option[T]): Unit = {
    writeOptAsDocument(fieldName, classTag[T].runtimeClass.asInstanceOf[Class[T]], value)
  }

  def get[T](fieldName: String)(implicit accessor: FieldAccessor[T]): T = accessor.get(document, fieldName)

  def getId[T](implicit accessor: FieldAccessor[T]): T = get(FieldAccessor.ID_FIELD_NAME)

  def getArray[T, C[_]](
      fieldName: String)(
      implicit accessor: FieldAccessor[T],
      cbf: CanBuildFrom[Nothing, T, C[T]]): C[T] = {
    FieldAccessor.getArray(document, fieldName)
  }

  def readFromDocument[T](fieldName: String, valueType: Class[T]): T

  def readArrayFromDocument[T, C[_]](
      fieldName: String,
      valueType: Class[T])(
      implicit cbf: CanBuildFrom[Nothing, T, C[T]]): C[T]

  def readOptFromDocument[T](fieldName: String, valueType: Class[T]): Option[T] = {
    if (!document.containsKey(fieldName) || document.isNull(fieldName)) {
      None
    }
    else {
      Some(readFromDocument(fieldName, valueType))
    }
  }

  def readFromDocument[T: ClassTag](fieldName: String): T = {
    readFromDocument(fieldName, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  }

  def readArrayFromDocument[T : ClassTag, C[_]](
      fieldName: String)(
      implicit cbf: CanBuildFrom[Nothing, T, C[T]]): C[T] = {
    readArrayFromDocument(fieldName, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  }

  def readOptFromDocument[T: ClassTag](fieldName: String): Option[T] = {
    readOptFromDocument(fieldName, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  }
}
