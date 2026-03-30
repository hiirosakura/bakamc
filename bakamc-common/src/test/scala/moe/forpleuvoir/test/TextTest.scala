package moe.forpleuvoir.test

import moe.forpleuvoir.bakamc.common.extension.Literal
import org.junit.jupiter.api.Test

class TextTest {

  @Test
  def test(): Unit = {
    val component = Literal("aaaa")
    println(component.content())
  }

}
