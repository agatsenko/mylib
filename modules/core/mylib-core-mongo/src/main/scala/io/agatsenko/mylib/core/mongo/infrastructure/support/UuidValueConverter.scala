/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-10-01
  */
package io.agatsenko.mylib.core.mongo.infrastructure.support

import scala.language.higherKinds

import scala.collection.generic.CanBuildFrom

import io.agatsenko.mylib.core.infrastructure.domain.UuidValue
import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.BsonNull
import org.mongodb.scala.bson.{BsonArray, BsonValue}

trait UuidValueConverter {
  import FieldAccessor.Implicit._

  def toBson(uuidValue: UuidValue): BsonValue = {
    if (uuidValue == null) BsonNull.VALUE else FieldAccessor.toBson(uuidValue.value)
  }

  def toBson[C[_]](
      uuidValues: Iterable[UuidValue])(
      implicit cbf: CanBuildFrom[Nothing, BsonValue, C[BsonValue]]): C[BsonValue] = {
    val bsonBuilder = cbf()
    if (uuidValues != null) {
      uuidValues.foreach(v => bsonBuilder += toBson(v))
    }
    bsonBuilder.result()
  }

  def toBsonArray(uuidValues: Iterable[UuidValue]): BsonArray = {
    if(uuidValues == null) new BsonArray else FieldAccessor.toBson(uuidValues.map(v => v.value))
  }
}
