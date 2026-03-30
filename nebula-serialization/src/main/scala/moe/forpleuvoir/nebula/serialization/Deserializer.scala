package moe.forpleuvoir.nebula.serialization

import moe.forpleuvoir.nebula.serialization.base.SerializeElement

trait Deserializer[T] {

  def deserialize(data: SerializeElement): T

}
