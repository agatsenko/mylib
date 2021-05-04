/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester.model

case class Employee(id: EmployeeId, position: String) extends Entity[EmployeeId] {
}
