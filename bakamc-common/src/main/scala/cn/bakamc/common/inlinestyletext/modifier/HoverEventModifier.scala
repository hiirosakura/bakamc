package cn.bakamc.common.inlinestyletext.modifier

import cn.bakamc.common.inlinestyletext.{Decoder, Lexer, ModifierContainer, TextModifier}
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.codec.Codec
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent

import scala.util.Try

case class HoverEventModifier private(
  private val modifiers: ModifierContainer
) extends TextModifier {


  override def modify(exp: String, origin: Component): Option[Component] = {
    if (exp == "h:null") {
      return Some {
        origin.hoverEvent(null)
      }
    }
    if (exp.startsWith(HoverEventModifier.HoverPrefix)) {
      val content = exp.substring(HoverEventModifier.hoverPrefixLength)
      if (content.nonEmpty) {
        Some {
          val hoverContent = Decoder.decode(Lexer.tokenize(content), modifiers)
          origin.hoverEvent(HoverEvent.showText(hoverContent))
        }
      } else None
    } else None
  }
}

object HoverEventModifier {

  private final val HoverPrefix = "h=>"
  private final val hoverPrefixLength = HoverPrefix.length

  final val DEFAULT = HoverEventModifier(
    ModifierContainer(
      LegacyChatFormattingModifier.DEFAULT,
      DecorationModifier.DEFAULT,
      ColorModifier.DEFAULT,
    )
  )

  given Codec[ModifierContainer] = ModifierContainer.CODEC

  final val CODEC: Codec[HoverEventModifier] = new Codec[HoverEventModifier] {
    override def deserialization(data: SerializeElement): Try[HoverEventModifier] = Try {
      HoverEventModifier(Codec.decode[ModifierContainer](data).get)
    }

    override def serialization(value: HoverEventModifier): SerializeElement = Codec.encode(value.modifiers)
  }

  given Codec[HoverEventModifier] = CODEC

  def apply(modifiers: ModifierContainer): HoverEventModifier =
    new HoverEventModifier(
      modifiers.filterNot {
        case _: HoverEventModifier => true
        case _: ClickEventModifier.type => true
        case _ => false
      }
    )

}