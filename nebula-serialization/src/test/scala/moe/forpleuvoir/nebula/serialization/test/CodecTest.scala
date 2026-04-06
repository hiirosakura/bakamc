package moe.forpleuvoir.nebula.serialization.test

import moe.forpleuvoir.nebula.serialization.codec.{Codec, given_Codec_Int}
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
    val so = buildSerObject{
      "a" := 15
      "_b?" := 666
      "d" := 17.0
    }
    println(so.deserialization[TestClass].get)
  }

}


class TestClass(val a: Int, val b: Float, val c: String, val d: Double) {
  override def toString: String = s"{a=$a, b=$b, c=$c, d=$d}"
}

object TestClass {
  val codec: Codec[TestClass] = Codec.create[TestClass]
    .fieldUsingCodec("a")(_.a)
    .field("_b?")(_.b)(Codec.Float(32, range = (0, 100)))
    .field("c")(_.c)(Codec.String("默认值"))
    .field("d")(_.d)(Codec.Double)
    .apply { (a, b, c, d) => TestClass(a, b, c, d) }

  given Codec[TestClass] = TestClass.codec
}