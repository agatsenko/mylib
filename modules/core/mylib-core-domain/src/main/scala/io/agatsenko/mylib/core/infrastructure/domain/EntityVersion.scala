/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.core.infrastructure.domain

final class EntityVersion private(val version: Long, val persistedVersion: Option[Long]) {
  def isTransient: Boolean = persistedVersion.isEmpty

  def hasChanges: Boolean = isTransient || (version != persistedVersion.get)

  def incrementIfUnchanged: EntityVersion = if (hasChanges) this else new EntityVersion(version + 1, persistedVersion)

  def acceptChanges: EntityVersion = if (hasChanges) new EntityVersion(version, Some(version)) else this
}

object EntityVersion {
  def apply(): EntityVersion = new EntityVersion(1, None)

  def apply(persistedVersion: Long): EntityVersion = new EntityVersion(persistedVersion, Some(persistedVersion))
}
