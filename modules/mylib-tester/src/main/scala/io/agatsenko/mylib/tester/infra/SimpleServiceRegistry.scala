/**
 * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
 * Created: 2020-02-09
 */
package io.agatsenko.mylib.tester.infra

import scala.collection.mutable
import scala.reflect.ClassTag

import io.mango.common.util.Check
import io.mango.services.ServiceLocator

class SimpleServiceRegistry extends ServiceLocator {
  private val _services: mutable.Map[ServiceKey[_], Any] = new mutable.HashMap()

  def register[T](serviceKey: ServiceTypeKey[T], service: T): SimpleServiceRegistry = {
    Check.argNotNull(serviceKey, "serviceKey")
    Check.argNotNull(service, "service")
    _services.put(serviceKey, service)
    this
  }

  def register(serviceKey: ServiceNameKey, service: Any): SimpleServiceRegistry = {
    Check.argNotNull(serviceKey, "serviceKey")
    Check.argNotNull(service, "service")
    _services.put(serviceKey, service)
    this
  }

  def register(service: Any): SimpleServiceRegistry = register(ServiceTypeKey(service), service)

  def register(name: String, service: Any): SimpleServiceRegistry = register(ServiceNameKey(name), service)

  def unregister(serviceKey: ServiceKey[_]): SimpleServiceRegistry = {
    _services.remove(serviceKey)
    this
  }

  def unregister(name: String): SimpleServiceRegistry = unregister(ServiceNameKey(name))

  def unregister(serviceType: Class[_]): SimpleServiceRegistry = {
    _services.filter(t => serviceType.isInstance(t._2)).foreach(t => _services.remove(t._1))
    this
  }

  def get[T](key: ServiceKey[_])(implicit serviceType: ClassTag[T]): Option[T] = {
    Check.argNotNull(key, "key")
    _services.get(key) match {
      case anyOpn: Some[Any] =>
        Check.state(
          serviceType.runtimeClass.isInstance(anyOpn.get),
          s"the found service cannot be cast to the specified type " +
          s"(found service: ${anyOpn.get}, expected service type: $serviceType)"
        )
        anyOpn.asInstanceOf[Some[T]]
      case None => None
    }
  }

  override def get[T](key: Any)(implicit serviceType: ClassTag[T]): Option[T] = {
    Check.argNotNull(key, "key")
    key match {
      case typedKey: ServiceTypeKey[T]@unchecked => get(typedKey)
      case typedKey: ServiceNameKey => get(typedKey)
      case _ => get(ServiceNameKey(key.toString))
    }
  }

  override def get[T](implicit serviceType: ClassTag[T]): Option[T] =
    get(ServiceTypeKey(serviceType.runtimeClass)).asInstanceOf[Option[T]].orElse {
      val iter = _services.values.filter(serviceType.runtimeClass.isInstance).iterator
      val found = iter.nextOption()
      Check.state(!iter.hasNext, s"found more than one service with $serviceType type")
      found.asInstanceOf[Option[T]]
    }

  override def getAll[T](implicit serviceType: ClassTag[T]): Seq[T] =
    _services.values.filter(serviceType.runtimeClass.isInstance).map(_.asInstanceOf[T]).toSeq
}
