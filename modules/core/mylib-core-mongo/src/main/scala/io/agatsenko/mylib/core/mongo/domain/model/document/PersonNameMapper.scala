/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-10-01
  */
package io.agatsenko.mylib.core.mongo.domain.model.document

import io.agatsenko.mylib.core.domain.model.document.PersonName
import io.agatsenko.mylib.core.mongo.infrastructure.mapper.{Mapper, MapperContext}

class PersonNameMapper extends Mapper[PersonName] {
  import io.agatsenko.mylib.core.mongo.infrastructure.mapper.FieldAccessor.Implicit._
  import PersonNameMapper.Fields._

  override def writeInsert(name: PersonName, context: MapperContext): Unit = {
    context.set(FIRST_NAME, name.firstName)
    context.set(LAST_NAME, name.lastName)
    context.set(MIDDLE_NAME, name.middleName)
  }

  override def writeUpdate(name: PersonName, context: MapperContext): Unit = {
    writeInsert(name, context)
  }

  override def read(context: MapperContext): PersonName = {
    PersonName(
      context.get[String](FIRST_NAME),
      context.get[String](LAST_NAME),
      context.get[Option[String]](MIDDLE_NAME)
    )
  }
}

object PersonNameMapper {
  object Fields {
    val FIRST_NAME = "firstName"
    val LAST_NAME = "lastName"
    val MIDDLE_NAME = "middleName"
  }
}
