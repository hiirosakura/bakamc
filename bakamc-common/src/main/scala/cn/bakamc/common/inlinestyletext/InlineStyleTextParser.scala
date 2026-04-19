package cn.bakamc.common.inlinestyletext

import cn.bakamc.common.inlinestyletext.modifier.{ColorModifier, DecorationModifier, LegacyChatFormattingModifier}
import moe.forpleuvoir.nebula.serialization.codec.Codec
import net.kyori.adventure.text.Component

case class InlineStyleTextParser(
  private val modifiers: ModifierContainer
) {

  def parse(raw: String): Component = {
    Decoder.decode(Lexer.tokenize(raw), modifiers)
  }

}

object InlineStyleTextParser {

  given Codec[ModifierContainer] = ModifierContainer.CODEC

  given Codec[InlineStyleTextParser] = InlineStyleTextParser.CODEC

  final val CODEC: Codec[InlineStyleTextParser] = Codec.delegated[InlineStyleTextParser, ModifierContainer](_.modifiers, InlineStyleTextParser(_))

  final val DEFAULT: InlineStyleTextParser = InlineStyleTextParser(ModifierContainer.DEFAULT)

  final val NONE_EVENT: InlineStyleTextParser = InlineStyleTextParser(ModifierContainer(
    LegacyChatFormattingModifier.DEFAULT,
    DecorationModifier.DEFAULT,
    ColorModifier.DEFAULT,
  ))

}