/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-30
  */
package io.agatsenko.mylib.core.mongo.infrastructure.support

import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor

trait CommonFields {
  val ID = FieldAccessor.ID_FIELD_NAME
  val VERSION = "persistedVersion"
}
