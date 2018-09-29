/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.domain.model

import scala.concurrent.{ExecutionContext, Future}

import io.agatsenko.mylib.core.infrastructure.domain.{EntityVersion, VersionedEntity}
import io.agatsenko.mylib.infrastructure.util.ToStringHelper
import io.agatsenko.mylib.infrastructure.concurrent.FutureExt.sync
import io.mango.common.util.Check

case class TagId(tagName: String) {
  Check.argNotNull(tagName, "tagName")
}

case class Tag private(
    id: TagId,
    version: EntityVersion,
    taggedDocuments: Set[DocumentId]) extends VersionedEntity[TagId] {
  Check.argNotNull(id, "id")
  Check.argNotNull(version, "version")
  Check.argNotNull(taggedDocuments, "taggedDocuments")

  def name: String = id.tagName

  def addDocuments(documents: DocumentId*)(implicit ec: ExecutionContext): Future[Tag] = {
    sync(Check.argNotNull(documents, "documents")).map(_ => copy(taggedDocuments = taggedDocuments ++ documents))
  }

  def removeDocuments(documents: DocumentId*)(implicit ec: ExecutionContext): Future[Tag] = {
    sync(Check.argNotNull(documents, "documents")).map(_ => copy(taggedDocuments = taggedDocuments -- documents))
  }

  override def toString: String = ToStringHelper(this).addAll(id -> "id", version -> "version").toString
}

object Tag {
  def apply(name: String)(implicit ec: ExecutionContext): Future[Tag] = {
    sync(new Tag(TagId(name), EntityVersion(), Set.empty))
  }

  def apply(
      id: TagId,
      persistedVersion: Long,
      taggedDocuments: Set[DocumentId])(
      implicit ec: ExecutionContext): Future[Tag] = {
    sync(new Tag(id, EntityVersion(persistedVersion), taggedDocuments))
  }
}
