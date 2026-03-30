package moe.forpleuvoir.nebula.serialization.extension

import moe.forpleuvoir.nebula.serialization.base.{Primitive, SerializeElement, SerializeObject}

def buildSerObject(block: SerializeObject ?=> Unit): SerializeObject = {
  val obj = SerializeObject()
  block(using obj)
  obj
}

inline def serObject(members: (String, SerializeElement | Primitive)*) = SerializeObject(members *)

extension (key: String) {
  infix def ->(value: SerializeElement | Primitive)(using obj: SerializeObject): Unit =
    obj.update(key, value)

}