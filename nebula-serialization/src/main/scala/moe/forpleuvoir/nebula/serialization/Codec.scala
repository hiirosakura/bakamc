package moe.forpleuvoir.nebula.serialization

trait Codec[T] extends Serializer[T], Deserializer[T]
