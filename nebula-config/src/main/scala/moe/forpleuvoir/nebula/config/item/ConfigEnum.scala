package moe.forpleuvoir.nebula.config.item

import moe.forpleuvoir.nebula.config.{ConfigItem, ConfigWithCodec}
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.codec.{Codec, JavaEnumCodec}

import scala.reflect.Enum as ScalaEnum

class ConfigJavaEnum[E <: Enum[E]](
  name: String,
  defaultValue: E
) extends ConfigItem[E](name, defaultValue) {

  override def serialization: SerializeElement = JavaEnumCodec.serialization(getValue)

  override def deserialization(element: SerializeElement): Unit =
    JavaEnumCodec.deserialization(getValue.getDeclaringClass, element).foreach(value => setValue(value))
}

object ConfigJavaEnum {
  def apply[E <: Enum[E]](name: String, defaultValue: E): ConfigJavaEnum[E] = new ConfigJavaEnum[E](name, defaultValue)
}

class ConfigEnum[T <: ScalaEnum](
  name: String,
  defaultValue: T,
  codec: Codec[T]
) extends ConfigWithCodec[T](name, defaultValue, codec)

object ConfigEnum {
  def apply[T <: ScalaEnum](name: String, defaultValue: T)(using codec: Codec[T]): ConfigEnum[T] = new ConfigEnum(name, defaultValue, codec)
}