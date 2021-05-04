/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester.model

import java.time.LocalDate

case class Person(
    id: PersonId,
    name: PersonName,
    birthDate: Option[LocalDate],
    phones: Set[PersonPhone]) extends Entity[PersonId]{
}
