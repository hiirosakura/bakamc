package moe.forpleuvoir.bakamc.common.extension

import net.kyori.adventure.text.*
import net.kyori.adventure.text.format.Style

import scala.collection.mutable

def Literal(content: String): TextComponent = Component.text(content)

def Translate(key: String, fallback: String | Null = null)(args: ComponentLike*): TranslatableComponent = Component.translatable(key, fallback, args *)

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