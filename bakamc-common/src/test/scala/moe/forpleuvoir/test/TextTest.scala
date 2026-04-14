package moe.forpleuvoir.test

import cn.bakamc.common.inlinestyletext.InlineStyleTextParser
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.Codec.serialization
import moe.forpleuvoir.nebula.serialization.hjson.HJsonDialect
import moe.forpleuvoir.nebula.serialization.json.JsonDialect
import org.junit.jupiter.api.Test

class TextTest {

  val text = "&{s[360 100 20],!i,b,c:run_command=>/test}这是文本 &{#ccff55}这是第二段文本"

  @Test
  def test(): Unit = {
    val parser = InlineStyleTextParser.DEFAULT
    val component = parser.parse(text)
    println(component)

    given Codec[InlineStyleTextParser] = InlineStyleTextParser.CODEC

    val s = parser.serialization
    val hs = HJsonDialect.encode(s)
    println(hs)
    println(HJsonDialect.parse(hs).get)
    val js = JsonDialect.encode(s)
    println(js)
    println(HJsonDialect.parse(js).get)
  }

}
