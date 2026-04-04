package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.base.SerializeElement

import scala.util.Try

trait Deserializer[T] {

  def deserialization(data: SerializeElement): Try[T]

}
