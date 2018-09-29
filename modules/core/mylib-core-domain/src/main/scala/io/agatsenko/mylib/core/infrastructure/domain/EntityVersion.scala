/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-16
  */
package io.agatsenko.mylib.core.infrastructure.domain

import io.agatsenko.mylib.infrastructure.util.ToStringHelper

final case class EntityVersion private(version: Long, persistedVersion: Option[Long]) {
  def isTransient: Boolean = persistedVersion.isEmpty

  def hasChanges: Boolean = isTransient || (version != persistedVersion.get)

  def incrementIfUnchanged: EntityVersion = if (hasChanges) this else new EntityVersion(version + 1, persistedVersion)

  def acceptChanges: EntityVersion = if (hasChanges) new EntityVersion(version, Some(version)) else this

  override def toString: String = {
    ToStringHelper(this).
        add(version, "version").
        add(persistedVersion, "persistedVersion").
        toString()
  }
}

object EntityVersion {
  def apply(): EntityVersion = new EntityVersion(1, None)

  def apply(persistedVersion: Long): EntityVersion = new EntityVersion(persistedVersion, Some(persistedVersion))
}
