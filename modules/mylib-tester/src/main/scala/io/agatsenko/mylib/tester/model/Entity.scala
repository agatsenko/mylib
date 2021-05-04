/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester.model

trait Entity[TId] extends Equals {
  def id: TId

  override def canEqual(that: Any): Boolean = that != null && that.getClass == getClass

  override def equals(obj: Any): Boolean = canEqual(obj) && obj.asInstanceOf[Entity[TId]].id == id
}
