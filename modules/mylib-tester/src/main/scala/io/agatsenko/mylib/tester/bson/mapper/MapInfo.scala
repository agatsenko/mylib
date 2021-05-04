/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-03-09
 */
package io.agatsenko.mylib.tester.bson.mapper

case class MapInfo[T](modelType: Class[T], fieldMaps: Seq[FieldMap[_]]) {
  def name: String = modelType.getSimpleName
}
