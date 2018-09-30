/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-15
  */
package io.agatsenko.mylib.core.domain.model.document

import java.util.UUID

import io.agatsenko.mylib.core.infrastructure.domain.{EntityVersion, VersionedEntity}
import io.agatsenko.mylib.infrastructure.util.ToStringHelper
import io.mango.common.util.Check
import monix.eval.Task

case class DocumentId(value: UUID) {
  Check.argNotNull(value, "value")
}

object DocumentId {
  def apply(): DocumentId = new DocumentId(UUID.randomUUID())
}

trait DocumentRepository {
  def get(id: DocumentId): Task[Option[Document]]

  def save(doc: Document): Task[Document]
}

class Document private(
    val id: DocumentId,
    val version: EntityVersion,
    val title: String,
    val description: Option[String],
    val authors: Set[AuthorId],
    val tags: Set[TagId]) extends VersionedEntity[DocumentId] {
  Check.argNotNull(id, "id")
  Check.argNotNull(version, "version")
  Check.argNotNullOrEmpty(title, "title")
  Check.argNotNull(description, "description")
  Check.argNotNull(authors, "authors")
  Check.argNotNull(tags, "tags")

  def modify(
      title: String = this.title,
      description: Option[String] = this.description,
      authors: Set[AuthorId] = this.authors,
      tags: Set[TagId] = this.tags): Document = {
    if (title == this.title && description == this.description && authors == this.authors && tags == this.tags ) {
      this
    }
    else {
      new Document(id, version.incrementIfUnchanged, title, description, authors, tags)
    }
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

  override def toString: String = {
    ToStringHelper(this).
        addAll(id -> "id", version -> "version", title -> "title", description -> "description", tags -> "tags").
        toString
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
