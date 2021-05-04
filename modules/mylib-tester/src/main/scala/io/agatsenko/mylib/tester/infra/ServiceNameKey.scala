/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester.infra

import io.mango.common.util.Check

case class ServiceNameKey private(value: String) extends ServiceKey[String] {
  Check.argNotEmpty(value, "value")
}
