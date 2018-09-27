/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-27
  */
package io.agatsenko.mylib.core.mongo.infrastructure.mapper

trait Mapper[T] {
  def write(entity: T, context: MapperContext): Unit

  def read(context: MapperContext): T
}
