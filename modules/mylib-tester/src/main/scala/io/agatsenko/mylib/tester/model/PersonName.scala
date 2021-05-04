/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester.model

case class PersonName(surname: String, name: String, patronymic: Option[String]) {
  def fullName: String = patronymic match {
    case Some(v) => s"$surname $name $v"
    case _ => s"$surname $name"
  }
}
