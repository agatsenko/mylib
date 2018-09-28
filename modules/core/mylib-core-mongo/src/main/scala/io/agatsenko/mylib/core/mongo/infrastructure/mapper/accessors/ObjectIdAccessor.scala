/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper.accessors

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor
import org.bson.types.ObjectId
import org.bson.BsonDocument
import org.mongodb.scala.bson.BsonObjectId

class ObjectIdAccessor extends FieldAccessor[ObjectId] {
  override def set(doc: BsonDocument, name: String, value: ObjectId): Unit = doc.put(name, new BsonObjectId(value))

  override def get(doc: BsonDocument, name: String): ObjectId = doc.getObjectId(name).getValue
}
