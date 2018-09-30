/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-30
  */
package io.agatsenko.mylib.core.domain.model.document

import java.time.LocalDate
import java.util.UUID

import io.agatsenko.mylib.core.infrastructure.domain.{EntityVersion, UuidValue, VersionedEntity}
import io.agatsenko.mylib.infrastructure.util.ToStringHelper
import io.mango.common.util.Check
import monix.eval.Task

case class AuthorId(value: UUID) extends UuidValue {
  Check.argNotNull(value, "value")
}

object AuthorId {
  def apply(): AuthorId = AuthorId(UUID.randomUUID())
}

trait AuthorRepository {
  def get(id: AuthorId): Task[Option[Author]]

  def getSeveral(ids: Seq[AuthorId]): Task[Iterable[Author]]

  def save(author: Author): Task[Author]
}

class Author private(
    val id: AuthorId,
    val version: EntityVersion,
    val name: PersonName,
    val description: Option[String],
    val dateOfBirth: Option[LocalDate]) extends VersionedEntity[Author] {
  type TId = AuthorId

  Check.argNotNull(id, "id")
  Check.argNotNull(version, "version")
  Check.argNotNull(name, "name")
  Check.argNotNull(description, "description")
  Check.argNotNull(dateOfBirth, "dateOfBirth")

  def modify(
      name: PersonName = this.name,
      description: Option[String] = this.description,
      dateOfBirth: Option[LocalDate] = this.dateOfBirth): Author = {
    copy(name = name, description = description, dateOfBirth = dateOfBirth)
  }

  override def acceptChanges: Author = copy(version = version.acceptChanges)

  override def toString: String = {
    ToStringHelper(this).
        addAll(
          id -> "id",
          version -> "version",
          name -> "name",
          description -> "description",
          dateOfBirth -> "dateOfBirth"
        ).
        toString
  }

  private def copy(
      version: EntityVersion = this.version,
      name: PersonName = this.name,
      description: Option[String] = this.description,
      dateOfBirth: Option[LocalDate] = this.dateOfBirth): Author = {
    if (version == this.version &&
        name == this.name &&
        description == this.description &&
        dateOfBirth == this.dateOfBirth) {
      this
    }
    else {
      new Author(id, version, name, description, dateOfBirth)
    }
  }
}

object Author {
  def apply(name: PersonName, description: Option[String], dateOfBirth: Option[LocalDate]): Author = {
    new Author(AuthorId(), EntityVersion(), name, description, dateOfBirth)
  }

  def apply(
      id: AuthorId,
      persistedVersion: Long,
      name: PersonName,
      description: Option[String],
      dateOfBirth: Option[LocalDate]): Author = {
    new Author(id, EntityVersion(persistedVersion), name, description, dateOfBirth)
  }
}
