/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-27
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

import scala.reflect.{classTag, ClassTag}

import org.bson.BsonDocument

trait MapperContext {
  import MapperContext._

  def document: BsonDocument

  def getMapper[T](valueType: Class[T]): Mapper[T]

  def getMapper[T: ClassTag]: Mapper[T] = getMapper(classTag[T].runtimeClass.asInstanceOf[Class[T]])

  def set[T](filedName: String, value: T)(implicit writer: FieldWriter[T]): Unit = {
    writer.write(document, filedName, value)
  }

  def setId[T](value: T)(implicit writer: FieldWriter[T]): Unit = set(ID_FIELD_NAME, value)

  def write[T](fieldName: String, valueType: Class[T], value: T): Unit

  def writeOpt[T](fieldName: String, valueType: Class[T], valueOpt: Option[T]): Unit

  def write[T: ClassTag](fieldName: String, value: T): Unit = {
    write(fieldName, classTag[T].runtimeClass.asInstanceOf[Class[T]], value)
  }

  def writeOpt[T: ClassTag](fieldName: String, value: Option[T]): Unit = {
    writeOpt(fieldName, classTag[T].runtimeClass.asInstanceOf[Class[T]], value)
  }

  def get[T](fieldName: String)(implicit reader: FieldReader[T]): T = reader.read(document, fieldName)

  def getId[T](implicit reader: FieldReader[T]): T = get(ID_FIELD_NAME)

  def read[T](fieldName: String, valueType: Class[T]): T

  def readOpt[T](fieldName: String, valueType: Class[T]): Option[T]

  def read[T: ClassTag](fieldName: String): T = read(fieldName, classTag[T].runtimeClass.asInstanceOf[Class[T]])

  def readOpt[T: ClassTag](fieldName: String): Option[T] = {
    readOpt(fieldName, classTag[T].runtimeClass.asInstanceOf[Class[T]])
  }
}

private[mapper] object MapperContext {
  val ID_FIELD_NAME = "_id"
}