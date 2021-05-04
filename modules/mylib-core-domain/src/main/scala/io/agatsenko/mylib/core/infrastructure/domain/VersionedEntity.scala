/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.core.infrastructure.domain

trait VersionedEntity[TSelf] extends Entity {
  this: TSelf =>

  def version: EntityVersion

  def isTransient: Boolean = version.isTransient

  def hasChanges: Boolean = version.hasChanges

  def acceptChanges: TSelf

  override def equals(any: Any): Boolean = {
    if (super.equals(any)) {
      val versionedEntity = any.asInstanceOf[VersionedEntity[TSelf]]
      version == versionedEntity.version
    }
    else {
      false
    }
  }

  override def hashCode(): Int = id.hashCode() ^ version.hashCode()
}
