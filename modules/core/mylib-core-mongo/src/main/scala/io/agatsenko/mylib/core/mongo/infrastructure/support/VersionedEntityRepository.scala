/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-10-01
  */
package io.agatsenko.mylib.core.mongo.infrastructure.support

import scala.compat.Platform.ConcurrentModificationException
import scala.concurrent.Future

import io.agatsenko.mylib.core.infrastructure.domain.VersionedEntity
import io.agatsenko.mylib.core.mongo.infrastructure.mapper.MapperRegistry
import io.mango.common.util.Check
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.conversions.Bson

trait VersionedEntityRepository {
  type TEntity <: VersionedEntity[TEntity]

  def save(entity: TEntity): Future[TEntity] = {
    Check.argNotNull(entity, "entity")
    if (entity.hasChanges) insert(entity) else update(entity)
  }

  protected def mapperRegistry: MapperRegistry

  protected def collection: MongoCollection[TEntity]

  protected def updateFilter(entity: TEntity): Bson

  def insert(entity: TEntity): Future[TEntity] = {
    collection.insertOne(entity).map(_ => entity.acceptChanges).head()
  }

  protected def update(entity: TEntity): Future[TEntity] = {
    collection.
        updateOne(updateFilter(entity), mapperRegistry.createUpdateDocument(entity)).
        map { result =>
          if (!result.wasAcknowledged) {
            throw new ConcurrentModificationException(s"update entity: $entity")
          }
          entity.acceptChanges
        }.
        head()
  }
}
