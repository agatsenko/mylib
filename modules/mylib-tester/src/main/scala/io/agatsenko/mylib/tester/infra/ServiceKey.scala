/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester.infra

import java.util.Objects

trait ServiceKey[TValue] extends Any with Equals {
  def value: TValue

  override def canEqual(that: Any): Boolean = that != null && that.getClass == getClass

  override def equals(obj: Any): Boolean = canEqual(obj) && obj.asInstanceOf[ServiceKey[TValue]].value == value

  override def hashCode(): Int = Objects.hashCode(value)

  override def toString: String = s"${getClass.getSimpleName}($value)"
}
