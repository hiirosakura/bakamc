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


  @Test
  def t2(): Unit = {
    val user = Student(Some("forpleuvoir"), Some(18), true)
    val ts = user.serialization
    println(ts)
    println(ts.deserialization[Student].get)
    val so = buildSerObject {
      "name" := "forpleuvoir"
      //      "age" := 18
    }
    println(so.deserialization[Student].get)
  }

}


class TestClass(val a: Int, val b: Float, val c: String, val d: Double) {
  override def toString: String = s"{a=$a, b=$b, c=$c, d=$d}"
}

object TestClass {

  given Codec[Float] = Codec.Float

  val codec: Codec[TestClass] = Codec.create[TestClass]
    .field("a").getter(_.a).codec(using Codec.Int)
    .field("_b?").getter(_.b).default(15.0f).codec
    .field("sd").getter(_.c).default("sda").codec(using Codec.String)
    .field("ddd?").getter(_.d).default(15.0).codec(using Codec.Double)
    .build((a, b, c, d) => TestClass(a, b, c, d))

  given Codec[TestClass] = TestClass.codec
}

case class Student(name: Option[String], age: Option[Int], sex: Boolean)

object Student {

  given Codec[Boolean] = Codec.Boolean

  given Codec[Student] = codec


  val codec: Codec[Student] = Codec.create[Student]
    .field("name").getter(_.name).default(Some("66")).optionCodec(using Codec.String)
    .field("age").default(Option(21)).getter(_.age).optionCodec(using Codec.Int)
    .field("sex").getter(_.sex).default(false).codec
    .build((name, age, sex) => Student(name, age, sex))
}