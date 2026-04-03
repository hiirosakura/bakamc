package moe.forpleuvoir.nebula.serialization

import moe.forpleuvoir.nebula.serialization.base.SerializeElement

trait Serializer[T] {

  def serialization(value: T): SerializeElement

}
