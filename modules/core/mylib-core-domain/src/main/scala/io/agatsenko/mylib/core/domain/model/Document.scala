/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-15
  */
package io.agatsenko.mylib.core.domain.model

import scala.concurrent.{ExecutionContext, Future}

import java.util.UUID

import io.agatsenko.mylib.core.infrastructure.domain.{EntityVersion, VersionedEntity}
import io.agatsenko.mylib.infrastructure.concurrent.FutureExt.sync
import io.agatsenko.mylib.infrastructure.util.ToStringHelper
import io.mango.common.util.Check
import io.mango.services.Services

case class DocumentId(value: UUID) {
  Check.argNotNull(value, "value")
}

case class Document private(
    id: DocumentId,
    version: EntityVersion,
    title: String,
    description: Option[String],
    tags: Set[TagId]) extends VersionedEntity[DocumentId] {
  Check.argNotNull(id, "id")
  Check.argNotNull(version, "version")
  Check.argNotNullOrEmpty(title, "title")
  Check.argNotNull(description, "description")
  Check.argNotNull(tags, "tags")

  def modify(
      title: String = this.title,
      description: Option[String] = this.description,
      tags: Set[TagId] = this.tags)(
      implicit ec: ExecutionContext): Future[Document] = sync {
    if (title == this.title && description == this.description && this.tags == tags) {
      this
    }
    else {
      copy(id, version.incrementIfUnchanged, title, description, tags)
    }
  }

  def addTags(tags: TagId*)(implicit ec: ExecutionContext): Future[Document] = {
    sync(Check.argNotNull(tags, "tags")).flatMap(_ => modify(tags = this.tags -- tags))
  }

  def removeTags(tags: TagId*)(implicit ec: ExecutionContext): Future[Document] = {
    sync(Check.argNotNull(tags, "tags")).flatMap(_ => modify(tags = this.tags -- tags))
  }

  def clearTags(implicit ec: ExecutionContext): Future[Document] = modify(tags = Set.empty)

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
      tags: Iterable[TagId] = Iterable.empty)(
      implicit ec: ExecutionContext): Future[Document] = {
    sync(Services.get[DocumentRepository].get).
        flatMap(_.newId).
        map(new Document(_, EntityVersion(), title, description, tags.toSet))
  }

  def apply(
      id: DocumentId,
      persistedVersion: Long,
      title: String,
      description: Option[String],
      tags: Set[TagId])(
      implicit ec: ExecutionContext): Future[Document] = sync {
    new Document(id, EntityVersion(persistedVersion), title, description, tags)
  }
}
