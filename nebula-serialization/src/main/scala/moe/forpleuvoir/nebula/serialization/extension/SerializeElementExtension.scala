package moe.forpleuvoir.nebula.serialization.extension

import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.{Deserializer, Serializer}

extension [T](self: T) {

  def serialize[S <: Serializer[T]]()(using serializer: S): SerializeElement = serializer.serialize(self)

  def deserialize(element: SerializeElement)(using serializer: Deserializer[T]): T = serializer.deserialize(element)

}