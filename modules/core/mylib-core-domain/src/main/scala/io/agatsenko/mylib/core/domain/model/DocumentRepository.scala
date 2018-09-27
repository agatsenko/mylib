/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.core.domain.model

import scala.concurrent.Future

trait DocumentRepository {
  def newId: Future[DocumentId]

  def get(id: DocumentId): Future[Option[Document]]

  def save(doc: Document): Future[Document]
}
