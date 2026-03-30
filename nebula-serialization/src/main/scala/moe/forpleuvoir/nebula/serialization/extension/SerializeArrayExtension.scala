package moe.forpleuvoir.nebula.serialization.extension

import moe.forpleuvoir.nebula.serialization.base.{Primitive, SerializeArray, SerializeElement, SerializePrimitive}

def buildSerArray(block: SerializeArray ?=> Unit): SerializeArray = {
  val array = SerializeArray()
  block(using array)
  array
}

inline def serArray(elements: SerializeElement | Primitive*) = SerializeArray(elements *)

def add(value: Primitive)(using array: SerializeArray) = array.add(value)

def add(value: SerializeElement)(using array: SerializeArray) = array.addOne(value)

def insert(idx: Int, value: Primitive)(using array: SerializeArray): Unit =
  array.insert(idx, SerializePrimitive.of(value))

def insert(idx: Int, value: SerializeElement)(using array: SerializeArray): Unit = array.insert(idx, value)

def remove(idx: Int)(using array: SerializeArray) = array.remove(idx)

def remove(idx: Int, count: Int)(using array: SerializeArray): Unit = array.remove(idx, count)

def addAll(elems: IterableOnce[SerializeElement])(using array: SerializeArray) = array.addAll(elems)

def addAll(elems: SerializeArray)(using array: SerializeArray) = array.addAll(elems)

def addAll(elems: Primitive*)(using array: SerializeArray): Unit = array.addAll(elems *)

def clear(using array: SerializeArray): Unit = array.clear()

