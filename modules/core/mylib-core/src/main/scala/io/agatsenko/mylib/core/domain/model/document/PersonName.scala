/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-30
  */
package io.agatsenko.mylib.core.domain.model.document

import io.mango.common.util.Check

case class PersonName(firstName: String, lastName: String, middleName: Option[String] = None) {
  Check.argNotNullOrEmpty(firstName, "firstName")
  Check.argNotNullOrEmpty(lastName, "lastName")

  override def toString: String = {
    middleName.
        map(mn => s"${getClass.getSimpleName}($firstName $mn $lastName)").
        getOrElse(s"${getClass.getSimpleName}($firstName $lastName)")
  }
}
