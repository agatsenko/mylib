/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-30
  */
package io.agatsenko.mylib.core.persist

trait Transactional {
  type TTxContext <: TxContext

  def currentTxContext: Option[TTxContext]

  def withinTx[R](f: TTxContext => R): R
}
