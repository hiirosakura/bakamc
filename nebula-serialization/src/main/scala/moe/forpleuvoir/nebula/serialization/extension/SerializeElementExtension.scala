package moe.forpleuvoir.nebula.serialization.extension

import moe.forpleuvoir.nebula.serialization.base.{Primitive, SerializeElement, SerializeNull, SerializePrimitive}
import moe.forpleuvoir.nebula.serialization.codec.{Deserializer, Serializable, Serializer}

import scala.util.Try

extension [T](self: T) {
  /**
   * 对于有序列化器的[Serializer]对象来进行序列化
   *
   * @param serializer 序列化器
   * @tparam S
   * @return
   */
  inline def serialization[S <: Serializer[T]](using serializer: S): SerializeElement = serializer.serialization(self)

  def toSerializeElement: SerializeElement = {
    self match {
      case null => SerializeNull
      case se: SerializeElement => se
      case p: Primitive => SerializePrimitive(p)
      case map: Map[_, _] => serObject(map.map((k, v) => k.toString -> v.toSerializeElement))
      case ite: Iterable[_] => serArray(ite.map(_.toSerializeElement))
      case array: Array[_] => serArray(array.map(_.toSerializeElement).toSeq)
      case je: java.lang.Enum[_] => SerializePrimitive(je.name)
      case s: Serializable => s.serialization
      case _ => throw new UnsupportedOperationException(s"Unsupported type ${self.getClass.getName}")
    }
  }

}

extension (element: SerializeElement) {
  /**
   * 反序列化为指定类型
   *
   * @param deserializer 反序列化器
   * @tparam T
   * @return
   */
  inline def deserialization[T](using deserializer: Deserializer[T]): Try[T] = deserializer.deserialization(element)
}
