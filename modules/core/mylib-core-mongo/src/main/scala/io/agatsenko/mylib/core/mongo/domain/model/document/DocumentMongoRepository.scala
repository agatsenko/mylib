/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-10-01
  */
package io.agatsenko.mylib.core.mongo.domain.model.document

import java.util.UUID

import io.agatsenko.mylib.core.domain.model.document._
import io.agatsenko.mylib.core.mongo.infrastructure.mapper.{Mapper, MapperContext, MapperRegistry}
import io.agatsenko.mylib.core.mongo.infrastructure.support.{CommonFields, UuidValueConverter, VersionedEntityRepository}
import monix.eval.Task
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import org.mongodb.scala.bson.conversions.Bson

class DocumentMapper extends Mapper[Document] {
  import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor.Implicit._
  import DocumentMapper.Fields._

  override def writeInsert(doc: Document, context: MapperContext): Unit = {
    context.setId(doc.id.value)
    writeUpdate(doc, context)
  }

  override def writeUpdate(doc: Document, context: MapperContext): Unit = {
    context.set(VERSION, doc.version.persistedVersion)
    context.set(TITLE, doc.title)
    context.set(DESCRIPTION, doc.description)
    context.setArray(AUTHORS, doc.authors.map(_.value))
    context.setArray(TAGS, doc.tagNames)
  }

  override def read(context: MapperContext): Document = {
    Document(
      DocumentId(context.getId[UUID]),
      context.get[Long](VERSION),
      context.get[String](TITLE),
      context.get[Option[String]](DESCRIPTION),
      context.getArray[UUID, Set](AUTHORS).map(AuthorId(_)),
      context.getArray[String, Set](TAGS).map(TagId(_))
    )
  }
}

object DocumentMapper extends UuidValueConverter {

  val COLLECTION_NAME = "documents"

  object Fields extends CommonFields {
    val TITLE = "title"
    val DESCRIPTION = "description"
    val AUTHORS = "authors"
    val TAGS = "tags"
  }
}

class DocumentMongoRepository(
    db: MongoDatabase,
    protected val mapperRegistry: MapperRegistry) extends DocumentRepository with VersionedEntityRepository {
  import org.mongodb.scala.model.Filters._
  import DocumentMapper._
  import DocumentMapper.Fields._

  override type TEntity = Document

  protected val collection: MongoCollection[Document] = db.getCollection[Document](COLLECTION_NAME)

  override def get(id: DocumentId): Task[Option[Document]] = Task.deferFuture {
    collection.find(equal(ID, toBson(id))).headOption()
  }

  override protected def updateFilter(doc: Document): Bson = {
    and(equal(ID, toBson(doc.id)), equal(VERSION, doc.version.persistedVersion))
  }
}
