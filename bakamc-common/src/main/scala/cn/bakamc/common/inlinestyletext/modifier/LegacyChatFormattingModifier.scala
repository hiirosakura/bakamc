package cn.bakamc.common.inlinestyletext.modifier

import cn.bakamc.common.inlinestyletext.TextModifier
import cn.bakamc.common.inlinestyletext.modifier.LegacyChatFormattingModifier.DefaultCodes
import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializePrimitive}
import moe.forpleuvoir.nebula.serialization.codec.Codec
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.{NamedTextColor, ShadowColor, TextDecoration}

import scala.util.Try

case class LegacyChatFormattingModifier(
  prefix: Char = '$',
  allowedCodes: List[Char] = DefaultCodes
) extends TextModifier {

  private val validCodes: List[Char] = allowedCodes.intersect(LegacyChatFormattingModifier.DefaultCodes)

  override def modify(exp: String, origin: Component): Option[Component] = {
    // 检查是否符合前缀要求且长度为 2 (例如 $c)
    if (exp.length == 2 && exp.head == prefix) {
      val code = exp(1)
      if (validCodes.contains(code)) {
        // 返回对应的 Component 转换函数
        Some(applyLegacyCode(origin, code))
      } else None
    } else None
  }

  private def applyLegacyCode(component: Component, code: Char): Component = {
    code match {
      // 颜色代码
      case '0' => component.color(NamedTextColor.BLACK)
      case '1' => component.color(NamedTextColor.DARK_BLUE)
      case '2' => component.color(NamedTextColor.DARK_GREEN)
      case '3' => component.color(NamedTextColor.DARK_AQUA)
      case '4' => component.color(NamedTextColor.DARK_RED)
      case '5' => component.color(NamedTextColor.DARK_PURPLE)
      case '6' => component.color(NamedTextColor.GOLD)
      case '7' => component.color(NamedTextColor.GRAY)
      case '8' => component.color(NamedTextColor.DARK_GRAY)
      case '9' => component.color(NamedTextColor.BLUE)
      case 'a' => component.color(NamedTextColor.GREEN)
      case 'b' => component.color(NamedTextColor.AQUA)
      case 'c' => component.color(NamedTextColor.RED)
      case 'd' => component.color(NamedTextColor.LIGHT_PURPLE)
      case 'e' => component.color(NamedTextColor.YELLOW)
      case 'f' => component.color(NamedTextColor.WHITE)

      // 样式代码 (利用 Adventure 的 decoration)
      case 'k' => component.decoration(TextDecoration.OBFUSCATED, true)
      case 'l' => component.decoration(TextDecoration.BOLD, true)
      case 'm' => component.decoration(TextDecoration.STRIKETHROUGH, true)
      case 'n' => component.decoration(TextDecoration.UNDERLINED, true)
      case 'o' => component.decoration(TextDecoration.ITALIC, true)

      // 重置代码
      case 'r' =>
        component.color(null)
          .shadowColor(ShadowColor.none())
          .decoration(TextDecoration.BOLD, false)
          .decoration(TextDecoration.ITALIC, false)
          .decoration(TextDecoration.OBFUSCATED, false)
          .decoration(TextDecoration.STRIKETHROUGH, false)
          .decoration(TextDecoration.UNDERLINED, false)

      case _ => component
    }
  }
}

object LegacyChatFormattingModifier {

  val DefaultCodes: List[Char] = "0123456789abcdefklmonr".toList

  val DEFAULT: LegacyChatFormattingModifier = LegacyChatFormattingModifier()

  private given Codec[List[Char]] = new Codec[List[Char]] {
    override def deserialization(data: SerializeElement): Try[List[Char]] = Try {
      data.asString.get.toList
    }

    override def serialization(value: List[Char]): SerializeElement =
      SerializePrimitive(value.mkString)
  }

  val CODEC: Codec[LegacyChatFormattingModifier] = Codec.create[LegacyChatFormattingModifier]
    .field("prefix").getter(_.prefix).default('$').codec(Codec.Char)
    .field("allowed_codes").getter(_.allowedCodes).default(DefaultCodes).usingCodec
    .build((prefix, allowedCodes) =>
      LegacyChatFormattingModifier(prefix, allowedCodes)
    )

  given Codec[LegacyChatFormattingModifier] = CODEC
}