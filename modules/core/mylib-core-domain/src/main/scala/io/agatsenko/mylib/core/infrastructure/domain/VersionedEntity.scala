/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.core.infrastructure.domain

trait VersionedEntity[TId] extends Entity[TId] {
  def version: EntityVersion

  def isTransient: Boolean = version.isTransient

  def hasChanges: Boolean = version.hasChanges

  override def equals(any: Any): Boolean = {
    if (super.equals(any)) {
      val versionedEntity = any.asInstanceOf[VersionedEntity[TId]]
      version == versionedEntity.version
    }
    else {
      false
    }
  }

  override def hashCode(): Int = id.hashCode() ^ version.hashCode()
}
