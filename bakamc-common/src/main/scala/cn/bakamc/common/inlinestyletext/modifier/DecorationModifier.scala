package cn.bakamc.common.inlinestyletext.modifier

import cn.bakamc.common.inlinestyletext.TextModifier
import moe.forpleuvoir.nebula.serialization.codec.Codec.given_Codec_String
import moe.forpleuvoir.nebula.serialization.codec.{ArrayCodec, Codec}
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.{ShadowColor, TextDecoration}

import scala.jdk.CollectionConverters.*

case class DecorationModifier(
  private val decorationMapping: DecorationMapping
) extends TextModifier {

  override def modify(exp: String, origin: Component): Option[Component] = {
    if (exp.isEmpty) return None

    // 重置
    if (decorationMapping.rest.contains(exp)) return Some {
      origin.color(null)
        .shadowColor(ShadowColor.none())
        .decorations(Map(
          TextDecoration.BOLD -> TextDecoration.State.FALSE,
          TextDecoration.ITALIC -> TextDecoration.State.FALSE,
          TextDecoration.UNDERLINED -> TextDecoration.State.FALSE,
          TextDecoration.STRIKETHROUGH -> TextDecoration.State.FALSE,
          TextDecoration.OBFUSCATED -> TextDecoration.State.FALSE
        ).asJava)
    }

    val (cleanExp, state) =
      if (exp.startsWith("!")) (exp.substring(1), TextDecoration.State.FALSE)
      else if (exp.startsWith("?")) (exp.substring(1), TextDecoration.State.NOT_SET)
      else (exp, TextDecoration.State.TRUE)

    cleanExp match {
      case m if (decorationMapping.obfuscated.contains(m)) => Some(origin.decoration(TextDecoration.OBFUSCATED, state))
      case m if (decorationMapping.bold.contains(m)) => Some(origin.decoration(TextDecoration.BOLD, state))
      case m if (decorationMapping.italic.contains(m)) => Some(origin.decoration(TextDecoration.ITALIC, state))
      case m if (decorationMapping.strikethrough.contains(m)) => Some(origin.decoration(TextDecoration.STRIKETHROUGH, state))
      case m if (decorationMapping.underline.contains(m)) => Some(origin.decoration(TextDecoration.UNDERLINED, state))
      case _ => None
    }

  }

}

object DecorationModifier {

  final val DEFAULT: DecorationModifier = DecorationModifier(DecorationMapping.DEFAULT)

  final val CODEC = Codec.delegated[DecorationModifier, DecorationMapping](_.decorationMapping, DecorationModifier(_))

  given Codec[DecorationModifier] = CODEC

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

object DecorationMapping {
  val DEFAULT: DecorationMapping = DecorationMapping()
}