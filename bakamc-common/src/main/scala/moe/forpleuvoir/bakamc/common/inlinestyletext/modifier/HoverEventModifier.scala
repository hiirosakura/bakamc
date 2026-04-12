package moe.forpleuvoir.bakamc.common.inlinestyletext.modifier

import moe.forpleuvoir.bakamc.common.inlinestyletext.{Decoder, Lexer, TextModifier}
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent

case class HoverEventModifier(
  modifiers: List[TextModifier]
) extends TextModifier {

  private final val HoverPrefix = "h=>"

  // 预先过滤掉不支持的修饰符，避免在每次 modify 时重复过滤
  private val safeModifiers = modifiers.filterNot {
    case _: HoverEventModifier => true
    case m if m == ClickEventModifier => true
    case _ => false
  }

  override def modify(exp: String): Option[Component => Component] = {
    if (exp == "h:null") {
      return Some { c => c.hoverEvent(null) }
    }
    if (exp.startsWith(HoverPrefix)) {
      val content = exp.substring(HoverPrefix.length)
      if (content.nonEmpty) {
        Some { (c: Component) =>
          val hoverContent = Decoder.decode(Lexer.tokenize(content), safeModifiers)
          c.hoverEvent(HoverEvent.showText(hoverContent))
        }
      } else None
    } else None
  }
}
