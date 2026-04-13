package moe.forpleuvoir.test

import cn.bakamc.common.inlinestyletext.modifier.{ClickEventModifier, ColorModifier}
import cn.bakamc.common.inlinestyletext.{Decoder, Lexer}
import org.junit.jupiter.api.Test

class TextTest {

  val text = "&{s[360 100 20],!i,b,c:run_command=>/test}这是文本 &{#ccff55}这是第二段文本"

  @Test
  def test(): Unit = {
    val tokens = Lexer.tokenize(text)
    val component = Decoder.decode(tokens, List(ColorModifier(true, true),ClickEventModifier))
    println(component)
  }

}
