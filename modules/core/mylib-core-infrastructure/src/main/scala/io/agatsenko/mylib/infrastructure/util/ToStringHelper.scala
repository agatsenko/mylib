/**
  * Author: Alexander Gatsenko (alexandr.gatsenko@gmail.com)
  * Created: 2018-09-29
  */
package io.agatsenko.mylib.infrastructure.util

import java.util.Objects

import io.mango.common.util.Check

sealed class ToStringHelper private(private val className: String) {
  import ToStringHelper._

  Check.argNotNull(className, "className")

  private var valueHolders = List.empty[ValueHolder]

  def add(value: Any, name: String): ToStringHelper = {
    value match {
      case strValue: String => add(strValue, name)
      case _ =>
        valueHolders :+= new AnyFieldHolder(name, Objects.toString(value))
        this
    }
  }

  def add(value: String, name: String): ToStringHelper = {
    valueHolders :+= new StringFieldHolder(name, value)
    this
  }

  def add(value: Boolean, name: String): ToStringHelper = {
    valueHolders :+= new AnyFieldHolder(name, value.toString)
    this
  }

  def add(value: Byte, name: String): ToStringHelper = {
    valueHolders :+= new AnyFieldHolder(name, value.toString)
    this
  }

  def add(value: Short, name: String): ToStringHelper = {
    valueHolders :+= new AnyFieldHolder(name, value.toString)
    this
  }

  def add(value: Int, name: String): ToStringHelper = {
    valueHolders :+= new AnyFieldHolder(name, value.toString)
    this
  }

  def add(value: Long, name: String): ToStringHelper = {
    valueHolders :+= new AnyFieldHolder(name, value.toString)
    this
  }

  def add(value: Float, name: String): ToStringHelper = {
    valueHolders :+= new AnyFieldHolder(name, value.toString)
    this
  }

  def add(value: Double, name: String): ToStringHelper = {
    valueHolders :+= new AnyFieldHolder(name, value.toString)
    this
  }

  def add(value: Char, name: String): ToStringHelper = {
    valueHolders :+= new AnyFieldHolder(name, value.toString)
    this
  }

  def add(value: Any): ToStringHelper = {
    value match {
      case strValue: String => add(strValue)
      case _ =>
        valueHolders :+= new OneValueHolder(Objects.toString(value))
        this
    }
  }

  def add(value: String): ToStringHelper = {
    valueHolders :+= new OneStringValueHolder(value)
    this
  }

  def add(value: Boolean): ToStringHelper = {
    valueHolders :+= new OneValueHolder(value.toString)
    this
  }

  def add(value: Byte): ToStringHelper = {
    valueHolders :+= new OneValueHolder(value.toString)
    this
  }

  def add(value: Short): ToStringHelper = {
    valueHolders :+= new OneValueHolder(value.toString)
    this
  }

  def add(value: Int): ToStringHelper = {
    valueHolders :+= new OneValueHolder(value.toString)
    this
  }

  def add(value: Long): ToStringHelper = {
    valueHolders :+= new OneValueHolder(value.toString)
    this
  }

  def add(value: Float): ToStringHelper = {
    valueHolders :+= new OneValueHolder(value.toString)
    this
  }

  def add(value: Double): ToStringHelper = {
    valueHolders :+= new OneValueHolder(value.toString)
    this
  }

  def add(value: Char): ToStringHelper = {
    valueHolders :+= new OneValueHolder(value.toString)
    this
  }

  def addAll(fields: (Any, String)*): ToStringHelper = {
    Check.argNotNull(fields, "fields")
    fields.foreach(f => add(f._1, f._2))
    this
  }

  override def toString: String = valueHolders.mkString(className + "{", ", ", "}")
}

object ToStringHelper {
  def apply(className: String): ToStringHelper = new ToStringHelper(if (className == null) "" else className)

  def apply(clazz: Class[_]): ToStringHelper = ToStringHelper(clazz.getSimpleName)

  def apply(obj: AnyRef): ToStringHelper = {
    Check.argNotNull(obj, "obj")
    ToStringHelper(obj.getClass)
  }

  private[ToStringHelper] trait ValueHolder {
    override def toString: String
  }

  private[ToStringHelper] class AnyFieldHolder(name: String, value: String) extends ValueHolder {
    override def toString: String = name + ": " + value
  }

  private[ToStringHelper] class StringFieldHolder(name: String, value: String) extends ValueHolder {
    override def toString: String = name + ": '" + value + "'"
  }

  private[ToStringHelper] class OneValueHolder(val value: String) extends ValueHolder {
    override def toString: String = value
  }

  private[ToStringHelper] class OneStringValueHolder(val value: String) extends ValueHolder {
    override def toString: String = "'" + value + "'"
  }
}
