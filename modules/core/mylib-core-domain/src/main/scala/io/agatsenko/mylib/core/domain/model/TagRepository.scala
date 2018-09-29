/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-29
  */
package io.agatsenko.mylib.core.domain.model

import scala.concurrent.Future

trait TagRepository {
  def get(name: String): Future[Option[Tag]]

  def save(tag: Tag): Future[Tag]
}
