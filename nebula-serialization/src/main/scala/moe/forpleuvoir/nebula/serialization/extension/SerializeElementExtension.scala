package moe.forpleuvoir.nebula.serialization.extension

import moe.forpleuvoir.nebula.serialization.base.{Primitive, SerializeElement, SerializeNull, SerializePrimitive}
import moe.forpleuvoir.nebula.serialization.{Deserializer, Serializer}

import scala.util.Try

extension [T](self: T) {
  /**
   * 对于有序列化器的[Serializer]对象来进行序列化
   *
   * @param serializer 序列化器
   * @tparam S
   * @return
   */
  def serialization[S <: Serializer[T]](using serializer: S): SerializeElement = serializer.serialization(self)

  def toSerializeElement: SerializeElement = {
    self match {
      case null => SerializeNull
      case se: SerializeElement => se
      case p: Primitive => SerializePrimitive(p)
      case map: Map[_, _] => serObject(map.map((k, v) => k.toString -> v.toSerializeElement))
      case ite:Iterable[_] => serArray(ite.map(_.toSerializeElement))
      case array:Array[_] => serArray(array.map(_.toSerializeElement).toSeq)
      case je:java.lang.Enum[_] => SerializePrimitive(je.name)
      case _ => ???
    }
  }

}

def deserialization[T](element: SerializeElement)(using serializer: Deserializer[T]): Try[T] = serializer.deserialization(element)
