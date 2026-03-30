package moe.forpleuvoir.test

import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.util.primitive.BooleanExtension.*
import org.junit.jupiter.api.Test

class ColorTest {

  @Test
  def test1(): Unit = {
    val c = Color.ofARGB(1.0, 173, 255, 0.6)
    println(c.toString)
    c.alpha(0.5f)
    c.red(.75f)
    c.green(222)
    c.blue(123)
    val a = c.clone()
    println(c.toString)
    var sad = 64
    sad += 31
    val d = c
    d += c
    println(d)

  }

}

