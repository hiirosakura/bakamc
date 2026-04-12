package moe.forpleuvoir.nebula.serialization.test

import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.extension.SerObjectOps.*
import moe.forpleuvoir.nebula.serialization.extension.{buildSerObject, deserialization, serialization}
import org.junit.jupiter.api.Test

class CodecTest {

  @Test
  def t1(): Unit = {
    val test = TestClass(1, 2.0f, "sd", 4.0)
    val ts = test.serialization
    println(ts)
    println(ts.deserialization[TestClass].get)
    val so = buildSerObject {
      "a" := 15
      "_b?" := 666
    }
    println(so.deserialization[TestClass].get)
  }

}


class TestClass(val a: Int, val b: Float, val c: String, val d: Double) {
  override def toString: String = s"{a=$a, b=$b, c=$c, d=$d}"
}

object TestClass {

  given Codec[Float] = Codec.Float

  val codec: Codec[TestClass] = Codec.create[TestClass]
    .field("a").getter(_.a).codec(Codec.Int)
    .field("_b?").getter(_.b).default(15.0f).usingCodec
    .field("sd").getter(_.c).default("sda").codec(Codec.String)
    .field("ddd?").getter(_.d).default(15.0).codec(Codec.Double)
    .build((a, b, c, d) => TestClass(a, b, c, d))

  given Codec[TestClass] = TestClass.codec
}