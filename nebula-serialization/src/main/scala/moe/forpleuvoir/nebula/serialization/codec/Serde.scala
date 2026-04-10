package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.base.SerializeElement

trait Serde extends Serializable with Deserializable

trait Serializable {

  def serialization: SerializeElement

}

trait Deserializable {

  def deserialization(data: SerializeElement): Unit

}
