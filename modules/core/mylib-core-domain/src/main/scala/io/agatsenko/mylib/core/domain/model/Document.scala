/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-15
  */
package io.agatsenko.mylib.core.domain.model

import java.util.UUID

import io.agatsenko.mylib.core.infrastructure.domain.{EntityVersion, VersionedEntity}
import io.mango.common.util.Check
import io.mango.services.Services

case class DocumentId private(value: UUID) extends AnyVal

object DocumentId {
  def apply(value: UUID): DocumentId = {
    Check.argNotNull(value, "value")
    new DocumentId(value)
  }
}

class Document private(
    val id: DocumentId,
    val version: EntityVersion,
    val title: String,
    val description: Option[String]) extends VersionedEntity[DocumentId] {
  Check.argNotNullOrEmpty(title, "title")
  Check.argNotNull(description, "description")
}

object Document {
  def apply(title: String, description: Option[String] = None): Document = {
    val repo = Services.get[DocumentRepository].get
//    new Document(repo.newId, EntityVersion(), title, description)
    // FIXME: not yet implemented
    ???
  }

  def apply(id: DocumentId, persistedVersion: Long, title: String, description: Option[String]): Document = {
    new Document(id, EntityVersion(persistedVersion), title, description)
  }
}
