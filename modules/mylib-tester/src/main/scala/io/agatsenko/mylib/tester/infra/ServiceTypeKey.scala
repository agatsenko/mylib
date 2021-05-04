/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester.infra

import io.mango.common.util.Check

case class ServiceTypeKey[TService] private(value: Class[TService]) extends ServiceKey[Class[TService]] {
  Check.argNotNull(value, "value")
}

object ServiceTypeKey {
  def apply[TService](service: TService): ServiceTypeKey[TService] = {
    Check.argNotNull(service, "service")
    new ServiceTypeKey[TService](service.getClass.asInstanceOf[Class[TService]])
  }
}
