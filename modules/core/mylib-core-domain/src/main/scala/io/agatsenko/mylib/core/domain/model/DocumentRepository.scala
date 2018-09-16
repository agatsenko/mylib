/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.core.domain.model

trait DocumentRepository {
  def newId: DocumentId

  def get(id: DocumentId): Option[Document]

  def save(doc: Document): Document
}
