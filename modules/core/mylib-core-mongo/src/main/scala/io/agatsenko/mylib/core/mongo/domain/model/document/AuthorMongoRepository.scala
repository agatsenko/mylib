/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-10-01
  */
package io.agatsenko.mylib.core.mongo.domain.model.document

import java.time.LocalDate
import java.util.UUID

import io.agatsenko.mylib.core.domain.model.document.{Author, AuthorId, AuthorRepository, PersonName}
import io.agatsenko.mylib.core.mongo.infrastructure.mapper.{Mapper, MapperContext, MapperRegistry}
import io.agatsenko.mylib.core.mongo.infrastructure.support.{CommonFields, VersionedEntityRepository}
import io.mango.common.util.Check
import monix.eval.Task
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import org.mongodb.scala.bson.conversions.Bson

class AuthorMapper extends Mapper[Author] {
  import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor.Implicit._
  import AuthorMapper.Fields._

  override def writeInsert(author: Author, context: MapperContext): Unit = {
    context.setId(author.id.value)
    writeUpdate(author, context)
  }

  override def writeUpdate(author: Author, context: MapperContext): Unit = {
    context.set(VERSION, author.version.persistedVersion)
    context.writeAsDocument(NAME, author)
    context.set(DESCRIPTION, author.description)
    context.set(DATE_OF_BIRTH, author.dateOfBirth)
  }

  override def read(context: MapperContext): Author = {
    Author(
      AuthorId(context.getId[UUID]),
      context.get[Long](VERSION),
      context.readFromDocument[PersonName](NAME),
      context.get[Option[String]](DESCRIPTION),
      context.get[Option[LocalDate]](DATE_OF_BIRTH)
    )
  }
}

object AuthorMapper {
  val COLLECTION_NAME = "authors"

  object Fields extends CommonFields {
    val NAME = "name"
    val DESCRIPTION = "description"
    val DATE_OF_BIRTH = "dateOfBirth"
  }
}

class AuthorMongoRepository(
    db: MongoDatabase,
    protected val mapperRegistry: MapperRegistry) extends AuthorRepository with VersionedEntityRepository {
  import org.mongodb.scala.model.Filters._
  import AuthorMapper._
  import AuthorMapper.Fields._

  type TEntity = Author

  protected val collection: MongoCollection[Author] = db.getCollection[Author](COLLECTION_NAME)

  override def get(id: AuthorId): Task[Option[Author]] = Task.deferFuture {
    Check.argNotNull(id, "id")
    collection.find(equal(ID, id.value)).headOption()
  }

  override def getSeveral(ids: Seq[AuthorId]): Task[Iterable[Author]] = Task.deferFuture {
    Check.argNotNullOrEmpty(ids, "ids")
    collection.find(in(ID, ids.map(_.value): _*)).toFuture()
  }

  override protected def updateFilter(author: Author): Bson = {
    and(equal(ID, author.id), equal(VERSION, author.version.persistedVersion))
  }
}
