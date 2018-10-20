/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.core.infrastructure.domain

trait Entity extends Equals {
  type TId

  def id: TId

  override def canEqual(any: Any): Boolean = any == null || getClass != any.getClass

  override def equals(any: Any): Boolean = {
    if (canEqual(any)) {
      val entity = any.asInstanceOf[Entity]
      id == entity.id
    }
    else {
      false
    }
  }

  override def hashCode(): Int = id.hashCode()
}
