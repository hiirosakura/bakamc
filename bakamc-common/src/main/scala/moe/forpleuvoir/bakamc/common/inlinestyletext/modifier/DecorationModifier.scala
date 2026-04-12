package moe.forpleuvoir.bakamc.common.inlinestyletext.modifier

import moe.forpleuvoir.bakamc.common.inlinestyletext.TextModifier
import moe.forpleuvoir.nebula.serialization.codec.{ArrayCodec, Codec, given_Codec_String}
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.{ShadowColor, TextDecoration}

case class DecorationModifier(
  decorationMapping: DecorationMapping
) extends TextModifier {

  override def modify(exp: String): Option[Component => Component] = {
    if (exp.isEmpty) return None

    val (cleanExp, state) =
      if (exp.startsWith("!")) (exp.substring(1), TextDecoration.State.FALSE)
      else if (exp.startsWith("?")) (exp.substring(1), TextDecoration.State.NOT_SET)
      else (exp, TextDecoration.State.TRUE)

    cleanExp match {
      case m if (decorationMapping.obfuscated.contains(m)) => Some { text => text.decoration(TextDecoration.OBFUSCATED, state) }
      case m if (decorationMapping.bold.contains(m)) => Some { text => text.decoration(TextDecoration.BOLD, state) }
      case m if (decorationMapping.italic.contains(m)) => Some { text => text.decoration(TextDecoration.ITALIC, state) }
      case m if (decorationMapping.strikethrough.contains(m)) => Some { text => text.decoration(TextDecoration.STRIKETHROUGH, state) }
      case m if (decorationMapping.underline.contains(m)) => Some { text => text.decoration(TextDecoration.UNDERLINED, state) }
      case m if (decorationMapping.rest.contains(m)) => Some { text =>
        text.color(null)
          .shadowColor(ShadowColor.none())
          .decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)
          .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
          .decoration(TextDecoration.UNDERLINED, TextDecoration.State.FALSE)
          .decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.FALSE)
          .decoration(TextDecoration.OBFUSCATED, TextDecoration.State.FALSE)
      }
      case _ => None
    }

  }

}


given Codec[Array[String]] = ArrayCodec[String]()

case class DecorationMapping(
  obfuscated: Array[String] = Array("o", "obfuscated"),
  bold: Array[String] = Array("b", "bold"),
  italic: Array[String] = Array("i", "italic"),
  strikethrough: Array[String] = Array("s", "strikethrough"),
  underline: Array[String] = Array("l", "underline"),
  rest: Array[String] = Array("r", "rest"),
) derives Codec