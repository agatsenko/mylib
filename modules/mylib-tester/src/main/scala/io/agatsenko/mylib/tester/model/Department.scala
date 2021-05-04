/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester.model

case class Department(
    id: DepartmentId,
    name: String,
    head: Employee,
    employees: Set[Employee]) extends Entity[DepartmentId] {
}
