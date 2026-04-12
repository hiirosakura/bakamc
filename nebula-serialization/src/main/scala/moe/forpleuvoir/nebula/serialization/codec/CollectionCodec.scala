package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.base.{SerializeArray, SerializeElement}
import moe.forpleuvoir.nebula.serialization.codec.Codec.{add, deserialization, serialization}

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.Try

class ArrayCodec[T](using ClassTag[T])(using codec: Codec[T]) extends Codec[Array[T]] {

  override def deserialization(data: SerializeElement): Try[Array[T]] = Try {
    data match {
      case array: SerializeArray =>
        array.map(e => e.deserialization[T].get).toArray
      case _ => throw new IllegalArgumentException("ArrayCodec: data is not a SerializeArray")
    }
  }

  override def serialization(value: Array[T]): SerializeElement = SerializeArray.build {
    value.foreach(e => add(e.serialization))
  }
}

class ListCodec[T](using ClassTag[T])(using codec: Codec[T]) extends Codec[List[T]] {

  override def deserialization(data: SerializeElement): Try[List[T]] = Try {
    data match {
      case array: SerializeArray =>
        array.map(e => e.deserialization[T].get).toList
      case _ => throw new IllegalArgumentException("ArrayCodec: data is not a SerializeArray")
    }
  }

  override def serialization(value: List[T]): SerializeElement = SerializeArray.build {
    value.foreach(e => add(e.serialization))
  }
}

class MutableBufferCodec[T](using ClassTag[T])(using codec: Codec[T]) extends Codec[mutable.Buffer[T]] {

  override def deserialization(data: SerializeElement): Try[mutable.Buffer[T]] = Try {
    data match {
      case array: SerializeArray =>
        array.map(e => e.deserialization[T].get)
      case _ => throw new IllegalArgumentException("ArrayCodec: data is not a SerializeArray")
    }
  }

  override def serialization(value: mutable.Buffer[T]): SerializeElement = SerializeArray.build {
    value.foreach(e => add(e.serialization))
  }
}

class SetCodec[T](using ClassTag[T])(using codec: Codec[T]) extends Codec[Set[T]] {

  override def deserialization(data: SerializeElement): Try[Set[T]] = Try {
    data match {
      case array: SerializeArray =>
        array.map(e => e.deserialization[T].get).toSet
      case _ => throw new IllegalArgumentException("ArrayCodec: data is not a SerializeArray")
    }
  }

  override def serialization(value: Set[T]): SerializeElement = SerializeArray.build {
    value.foreach(e => add(e.serialization))
  }
}