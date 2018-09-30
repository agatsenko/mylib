/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-28
  */
package io.agatsenko.mylib.core.domain.model.document

import io.agatsenko.mylib.core.infrastructure.domain.{EntityVersion, VersionedEntity}
import io.agatsenko.mylib.infrastructure.util.ToStringHelper
import io.mango.common.util.Check
import monix.eval.Task

case class TagId(tagName: String) {
  Check.argNotNull(tagName, "tagName")
}

trait TagRepository {
  def get(name: String): Task[Option[Tag]]

  def getSeveral(names: Seq[String]): Task[Iterable[Tag]]

  def save(tag: Tag): Task[Tag]
}

class Tag private(
    val id: TagId,
    val version: EntityVersion,
    val taggedDocuments: Set[DocumentId]) extends VersionedEntity[TagId] {
  Check.argNotNull(id, "id")
  Check.argNotNull(version, "version")
  Check.argNotNull(taggedDocuments, "taggedDocuments")

  def name: String = id.tagName

  def modify(taggedDocuments: Set[DocumentId] = this.taggedDocuments): Tag = {
    if (taggedDocuments == this.taggedDocuments) this else new Tag(id, version.incrementIfUnchanged, taggedDocuments)
  }

  def addDocuments(documents: DocumentId*): Tag = {
    modify(taggedDocuments = taggedDocuments ++ documents)
  }

  def removeDocuments(documents: DocumentId*): Tag = {
    Check.argNotNull(documents, "documents")
    modify(taggedDocuments = taggedDocuments -- documents)
  }

  override def toString: String = ToStringHelper(this).addAll(id -> "id", version -> "version").toString
}

object Tag {
  def apply(name: String): Tag = {
    new Tag(TagId(name), EntityVersion(), Set.empty)
  }

  def apply(id: TagId, persistedVersion: Long, taggedDocuments: Set[DocumentId]): Tag = {
    new Tag(id, EntityVersion(persistedVersion), taggedDocuments)
  }
}
