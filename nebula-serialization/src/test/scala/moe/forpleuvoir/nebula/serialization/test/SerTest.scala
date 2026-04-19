package moe.forpleuvoir.nebula.serialization.test

import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.serialization.TestEnum
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.codec.PrimitiveCodec.given
import moe.forpleuvoir.nebula.serialization.codec.{Codec, JavaEnumCodec, given}
import moe.forpleuvoir.nebula.serialization.extension.*
import moe.forpleuvoir.nebula.serialization.extension.SerArrayOps.*
import moe.forpleuvoir.nebula.serialization.extension.SerObjectOps.*
import org.junit.jupiter.api.Test

class SerTest {

  @Test
  def test1(): Unit = {
    val obj = buildSerObject {
      "key1" := "value1"
      "key2" := "v2"
    }
    val arr = serArray(
      "value1",
      obj,
      16,
      true,
    )
    val ar = buildSerArray {
      add(Color.fromARGB(0xFF66CCFF))
      add(18)
    }
    println(ar)

    println(16.serialization.asNumber.get)

    val obj2 = buildSerObject {
      "obj" := obj
      "arr" := arr
      "color" := Color.fromHSV(359, 50f, 100f)
    }
    println(obj2.toString())

    val a = SerializePrimitive(12)

    println(a.isNumber)

    val c = buildSerObject {
      "red" := 255
      "green" := 0.2
      "blue" := 1.0
    }
    val hc = buildSerObject {
      "hue" := 359
      "saturation" := 50
      "value" := 100
    }
    println(hc.deserialization[Color].get.display)

    var hsv = Color.fromHSV(359, 50f, 100f)
    println(hsv.serialization)

    println(hsv.display)

    val color: Color = c.deserialization[Color].get
    println(color.display)
    println(color.hue)
    println(color.saturation)
    println(color.value)
    val g = hsv + color
    hsv += color
    println(g.asString)
  }

  @Test
  def test2(): Unit = {
    val map = Map(
      "key1" -> "value1",
      "key2" -> "v2",
    )
    println(map.toSerializeElement)

    val arr = Array(
      "value1",
      map,
      16,
      true,
    )
    println(arr.toSerializeElement)
    val a = JavaEnumCodec.deserialization[TestEnum](SerializePrimitive("A")).get
    println(a)
    a.serialization

  }

  @Test
  def test3(): Unit = {
    val s1 = CE.GREEN.serialization
    println(s1)
    val c = s1.deserialization[CE]
    println(c)

    val u = User(name = "forpleuvoir", age = 18, color = CE.GREEN)
    val us = u.serialization
    println(us)
    val u2 = us.deserialization[User]
    println(u2)

    val t = (2, "forpleuvoir")
    val ts = t.serialization
    println(ts)
    val t2 = ts.deserialization[(Int, String)]
    println(t2)

  }

}


given Codec[(Int, String)] = Codec.derived

given Codec[CE] = Codec.derived

given Codec[User] = Codec.derived

enum CE {
  case RED, GREEN, BLUE
}

case class User(name: String, age: Int, color: CE)