/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-03-09
 */
package io.agatsenko.mylib.tester.bson.mapper

import io.mango.common.util.Check
import org.bson.BsonType
import org.bson.codecs.Codec

case class FieldMap[T](
    modelFieldName: String,
    modelFieldType: Class[T],
    docFieldName: String,
    docFieldType: BsonType,
    codec: Option[Codec[T]]) {
  Check.argNotEmpty(modelFieldName, "modelFieldName")
  Check.argNotNull(modelFieldType, "modelFieldType")
  Check.argNotEmpty(docFieldName, "docFieldName")
  Check.argNotNull(docFieldType, "docFieldType")
  Check.argNotNull(codec, "codec")
}
