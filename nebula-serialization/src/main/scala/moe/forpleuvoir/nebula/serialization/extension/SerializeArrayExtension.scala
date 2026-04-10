package moe.forpleuvoir.nebula.serialization.extension

import moe.forpleuvoir.nebula.serialization.base.{Primitive, SerializeArray, SerializeElement, SerializePrimitive}
import moe.forpleuvoir.nebula.serialization.codec.{Serializable, Serializer}

/**
 * need import moe.forpleuvoir.nebula.serialization.extension.SerArrayOps._
 *
 * @param block
 * @return
 */
def buildSerArray(block: SerializeArray ?=> Unit): SerializeArray = {
  val array = SerializeArray()
  block(using array)
  array
}

inline def serArray(elements: SerializeElement | Serializable | Primitive*) = SerializeArray(elements *)

def serArray[T](elements: T*)(using s: Serializer[T]) = {
  val array = SerializeArray()
  elements.foreach { elem =>
    array.addOne(s.serialization(elem))
  }
  array
}

def serArray(elements: Iterable[?]): SerializeArray = {
  val array = SerializeArray()
  elements.foreach { elem =>
    array.addOne(elem.toSerializeElement)
  }
  array
}

object SerArrayOps {

  def add(value: Primitive)(using array: SerializeArray): Unit = array.add(value)

  def add(value: SerializeElement)(using array: SerializeArray): Unit = array.addOne(value)

  def add[T](value: T)(using s: Serializer[T])(using array: SerializeArray): Unit = array.addOne(s.serialization(value))

  def insert(idx: Int, value: Primitive)(using array: SerializeArray): Unit =
    array.insert(idx, SerializePrimitive(value))

  def insert(idx: Int, value: SerializeElement)(using array: SerializeArray): Unit = array.insert(idx, value)

  def insert[T](idx: Int, value: T)(using s: Serializer[T])(using array: SerializeArray): Unit =
    array.insert(idx, s.serialization(value))

  def remove(idx: Int)(using array: SerializeArray): Unit = array.remove(idx)

  def remove(idx: Int, count: Int)(using array: SerializeArray): Unit = array.remove(idx, count)

  def addAll(elems: IterableOnce[SerializeElement | Primitive])(using array: SerializeArray): Unit = {
    elems.iterator.foreach {
      case elem: SerializeElement => array.addOne(elem)
      case elem: Primitive => array.add(elem)
    }
  }

  def addAll(elems: SerializeArray)(using array: SerializeArray): Unit = array.addAll(elems)

  def addAll(elems: Primitive*)(using array: SerializeArray): Unit = array.addAll(elems *)

  def addAll[T](elems: IterableOnce[T])(using s: Serializer[T])(using array: SerializeArray): Unit = {
    elems.iterator.foreach { elem =>
      array.addOne(s.serialization(elem))
    }
  }

  def clear(using array: SerializeArray): Unit = array.clear()

}
