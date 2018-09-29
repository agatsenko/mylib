/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-30
  */
package io.agatsenko.mylib.core.persist

trait TxContext {
  def isCompleted: Boolean

  def isRollbackOnly: Boolean

  def setRollbackOnly(): Unit
}
