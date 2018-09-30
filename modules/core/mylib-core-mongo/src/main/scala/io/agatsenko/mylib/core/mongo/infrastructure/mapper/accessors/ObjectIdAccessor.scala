/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.types.ObjectId
import org.bson.BsonDocument
import org.mongodb.scala.bson.{BsonObjectId, BsonValue}

class ObjectIdAccessor extends FieldAccessor[ObjectId] {
  override def from(bson: BsonValue): ObjectId = getValue(bson.asObjectId())

  override def to(value: ObjectId): BsonValue = new BsonObjectId(value)

  override def set(doc: BsonDocument, name: String, value: ObjectId): Unit = doc.put(name, to(value))

  override def get(doc: BsonDocument, name: String): ObjectId = getValue(doc.getObjectId(name))

  private def getValue(bson: BsonObjectId): ObjectId = bson.getValue
}
