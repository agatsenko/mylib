/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester.model

trait BaseRepo[TId, TEntity <: Entity[TId]] {
  def isPersistent(id: TId): Boolean

  def isPersistent(entity: TEntity): Boolean = isPersistent(entity.id)

  def find(id: TId): Option[TEntity]

  def save(entity: TEntity): TEntity

  def remove(id: TId): Boolean

  def remove(entity: TEntity): Boolean = remove(entity.id)
}
