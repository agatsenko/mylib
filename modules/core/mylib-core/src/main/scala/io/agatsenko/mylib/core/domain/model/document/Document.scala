/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-15
  */
package io.agatsenko.mylib.core.domain.model.document

import scala.concurrent.Future

import java.util.UUID

import io.mango.common.util.Check

import io.agatsenko.mylib.core.infrastructure.domain.{EntityVersion, UuidValue, VersionedEntity}
import io.agatsenko.mylib.infrastructure.util.ToStringHelper

case class DocumentId(value: UUID) extends UuidValue {
  Check.argNotNull(value, "value")
}

object DocumentId {
  def apply(): DocumentId = new DocumentId(UUID.randomUUID())
}

trait DocumentRepository {
  def get(id: DocumentId): Future[Option[Document]]

  def save(doc: Document): Future[Document]
}

class Document private(
    val id: DocumentId,
    val version: EntityVersion,
    val title: String,
    val description: Option[String],
    val authors: Set[AuthorId],
    val tags: Set[TagId]) extends VersionedEntity[Document] {
  type TId = DocumentId

  Check.argNotNull(id, "id")
  Check.argNotNull(version, "version")
  Check.argNotNullOrEmpty(title, "title")
  Check.argNotNull(description, "description")
  Check.argNotNull(authors, "authors")
  Check.argNotNull(tags, "tags")

  def tagNames: Set[String] = tags.map(_.tagName)

  def modify(
      title: String = this.title,
      description: Option[String] = this.description,
      authors: Set[AuthorId] = this.authors,
      tags: Set[TagId] = this.tags): Document = {
    copy(title = title, description = description, authors = authors, tags = tags)
  }

  def addAuthors(authors: AuthorId*): Document = {
    Check.argNotNull(authors, "authors")
    modify(authors = this.authors ++ authors)
  }

  def removeAuthors(authors: AuthorId*): Document = {
    Check.argNotNull(authors, "authors")
    modify(authors = this.authors -- authors)
  }

  def clearAuthors: Document = modify(authors = Set.empty)

  def addTags(tags: TagId*): Document = {
    Check.argNotNull(tags, "tags")
    modify(tags = this.tags -- tags)
  }

  def removeTags(tags: TagId*): Document = {
    Check.argNotNull(tags, "tags")
    modify(tags = this.tags -- tags)
  }

  def clearTags: Document = modify(tags = Set.empty)

  override def acceptChanges: Document = copy(version = version.acceptChanges)

  override def toString: String = {
    ToStringHelper(this).
        addAll(id -> "id", version -> "version", title -> "title", description -> "description", tags -> "tags").
        toString
  }

  private def copy(
      version: EntityVersion = this.version,
      title: String = this.title,
      description: Option[String] = this.description,
      authors: Set[AuthorId] = this.authors,
      tags: Set[TagId] = this.tags): Document = {
    if (version == this.version &&
        title == this.title &&
        description == this.description &&
        authors == this.authors &&
        tags == this.tags) {
      this
    }
    else {
      new Document(id, version.incrementIfUnchanged, title, description, authors, tags)
    }
  }
}

object Document {
  def apply(
      title: String,
      description: Option[String] = None,
      authors: Set[AuthorId] = Set.empty,
      tags: Set[TagId] = Set.empty): Document = {
    new Document(DocumentId(), EntityVersion(), title, description, authors, tags)
  }

  def apply(
      id: DocumentId,
      persistedVersion: Long,
      title: String,
      description: Option[String],
      authors: Set[AuthorId],
      tags: Set[TagId]): Document = {
    new Document(id, EntityVersion(persistedVersion), title, description, authors, tags)
  }
}
