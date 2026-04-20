package cn.bakamc.common.extension

import moe.forpleuvoir.nebula.common.color.Color
import net.kyori.adventure.text.*
import net.kyori.adventure.text.format.{Style, TextColor}

import scala.collection.mutable

extension (self: Component) {

  def flat(parentStyle: Style = Style.empty(), buffer: mutable.Buffer[Component] = mutable.Buffer.empty): List[Component] = {
    val effectiveStyle = self.style().merge(parentStyle, Style.Merge.Strategy.IF_ABSENT_ON_TARGET)
    val component: Component = self match {
      case t: TextComponent => Component.text(t.content())
      case t: TranslatableComponent => Component.translatable(t.key(), t.fallback(), t.arguments())
      case k: KeybindComponent => Component.keybind(k.keybind())
      case b: BlockNBTComponent => Component.blockNBT(b.nbtPath(), b.interpret(), b.separator(), b.pos())
      case e: EntityNBTComponent => Component.entityNBT(e.nbtPath(), e.selector())
      case s: StorageNBTComponent => Component.storageNBT(s.nbtPath(), s.interpret(), s.storage())
      case s: ScoreComponent => Component.score(s.name(), s.objective())
      case s: SelectorComponent => Component.selector(s.pattern(), s.separator())
      case other => other.children(java.util.Collections.emptyList())
    }
    buffer += component.style(effectiveStyle)
    val kids = self.children()
    if (!kids.isEmpty) {
      kids.forEach(child => child.flat(effectiveStyle, buffer))
    }
    buffer.toList
  }

}


object Text {

  given Conversion[Color, TextColor] = TextColor.color(_)

  given Conversion[Int, TextColor] = TextColor.color(_)

  def literal(content: String): TextComponent = Component.text(content)

  def translate(key: String, fallback: String | Null = null)(args: ComponentLike*): TranslatableComponent = Component.translatable(key, fallback, args *)

  def translate(key: String): TranslatableComponent = Component.translatable(key)

  def status(status: Boolean): TextComponent =
    if (status)
      Text.literal("[开启]").color(0x55FF55)
    else
      Text.literal("[关闭]").color(0xFF5555)

  def boolean(status: Boolean): TextComponent =
    if (status)
      Text.literal(status.toString).color(0x55FF55)
    else
      Text.literal(status.toString).color(0xFF5555)
}