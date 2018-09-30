/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-30
  */
package io.agatsenko.mylib.core.mongo.domain.model.document

import java.util.UUID

import io.agatsenko.mylib.core.domain.model.document.{DocumentId, Tag, TagId, TagRepository}
import io.agatsenko.mylib.core.mongo.infrastructure.mapper.{Mapper, MapperContext, MapperRegistry}
import io.agatsenko.mylib.core.mongo.infrastructure.support.{CommonFields, VersionedEntityRepository}
import io.mango.common.util.Check
import monix.eval.Task
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import org.mongodb.scala.bson.conversions.Bson

class TagMapper extends Mapper[Tag] {
  import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor.Implicit._
  import TagMapper.Fields._

  override def writeInsert(tag: Tag, context: MapperContext): Unit = {
    context.set(ID, tag.id.tagName)
    writeUpdate(tag, context)
  }

  override def writeUpdate(tag: Tag, context: MapperContext): Unit = {
    context.set(VERSION, tag.version.version)
    context.setArray(TAGGED_DOCUMENTS, tag.taggedDocuments.map(id => id.value))
  }

  override def read(context: MapperContext): Tag = {
    Tag(
      TagId(context.get[String](ID)),
      context.get[Long](VERSION),
      context.getArray[UUID, Set](TAGGED_DOCUMENTS).map(DocumentId(_))
    )
  }
}

object TagMapper {
  val COLLECTION_NAME = "tags"

  object Fields extends CommonFields {
    val NAME = ID
    val TAGGED_DOCUMENTS = "taggedDocuments"
  }
}

class TagMongoRepository(
    db: MongoDatabase,
    protected val mapperRegistry: MapperRegistry) extends TagRepository with VersionedEntityRepository {
  import org.mongodb.scala.model.Filters._
  import TagMapper._
  import TagMapper.Fields._

  type TEntity = Tag

  protected val collection: MongoCollection[Tag] = db.getCollection[Tag](COLLECTION_NAME)

  override def get(name: String): Task[Option[Tag]] = Task.deferFuture {
    Check.argNotNullOrEmpty(name, "name")
    collection.find(equal(NAME, name)).headOption()
  }

  override def getSeveral(names: Seq[String]): Task[Iterable[Tag]] = Task.deferFuture {
    Check.argNotNullOrEmpty(names, "names")
    collection.find(in(NAME, names: _*)).toFuture()
  }

  override protected def updateFilter(tag: Tag): Bson = {
    and(equal(ID, tag.id.tagName), equal(VERSION, tag.version.persistedVersion))
  }
}
