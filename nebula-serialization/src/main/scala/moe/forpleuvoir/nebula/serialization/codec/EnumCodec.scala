package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializePrimitive}
import moe.forpleuvoir.nebula.serialization.codec.{Codec, Serializer}

import java.lang.Enum as JEnum
import scala.reflect.ClassTag
import scala.util.Try

object JavaEnumCodec extends Serializer[JEnum[?]] {

  override def serialization(value: JEnum[?]): SerializeElement = SerializePrimitive(value.name())

  inline def deserialization[E <: JEnum[E]](data: SerializeElement)(using tag: ClassTag[E]): Try[JEnum[E]] = {
    val enumType = tag.runtimeClass.asInstanceOf[Class[E]]
    deserialization(enumType, data)
  }

  def deserialization[E <: JEnum[E]](enumType: Class[E], data: SerializeElement): Try[JEnum[E]] = Try {
    val name = data.asString.get
    try {
      Enum.valueOf(enumType, name)
    } catch {
      case _: Throwable =>
        enumType.getMethods.find { c => c.getName == "valueOf" && c.getReturnType == enumType } match {
          case Some(method) =>
            method.invoke(null, name).asInstanceOf[JEnum[E]]
          case None => enumType.getEnumConstants.find(_.name == name).get
        }
    }
  }

}

given Serializer[JEnum[?]] = JavaEnumCodec

class ScalaEnumCodec[E <: scala.reflect.Enum](val instances: Array[E]) extends Codec[E] {

  override def serialization(value: E): SerializeElement = SerializePrimitive(value.toString)

  override def deserialization(data: SerializeElement): Try[E] = Try {
    instances.find(_.toString == data.asString.get).get
  }
}


