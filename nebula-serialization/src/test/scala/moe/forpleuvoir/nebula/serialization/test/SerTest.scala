package moe.forpleuvoir.nebula.serialization.test

import moe.forpleuvoir.nebula.serialization.base.{SerializeObject, SerializePrimitive}
import moe.forpleuvoir.nebula.serialization.extension.*
import org.junit.jupiter.api.Test

class SerTest {

  @Test
  def test1(): Unit = {
    val obj = buildSerObject {
      "key1" -> "value1"
      "key2" -> "v2"
    }
    val arr = serArray(
      "value1",
      obj,
      16,
      true,
    )

    val obj2 = SerializeObject(("obj", obj), ("arr", arr))
    println(obj2.toString())

    val a = SerializePrimitive(12)

    println(a.isNumber)
  }

}
