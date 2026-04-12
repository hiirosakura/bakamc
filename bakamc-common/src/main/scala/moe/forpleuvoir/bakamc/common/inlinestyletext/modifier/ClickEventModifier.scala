package moe.forpleuvoir.bakamc.common.inlinestyletext.modifier

import moe.forpleuvoir.bakamc.common.inlinestyletext.TextModifier
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent

object ClickEventModifier extends TextModifier {

  // 使用正则提取组直接捕获 action 和 value
  private final val ClickPattern = """c:(open_url|open_file|run_command|suggest_command|change_page|copy_to_clipboard)=>(.+)""".r

  override def modify(exp: String): Option[Component => Component] =
    exp match {
      case e if e == "c:none" => Some(c => c.clickEvent(null))
      case ClickPattern(action, value) =>
        val clickEvent: Option[ClickEvent] = action match {
          case "open_url" => Some(ClickEvent.openUrl(value))
          case "open_file" => Some(ClickEvent.openFile(value))
          case "run_command" => Some(ClickEvent.runCommand(value))
          case "suggest_command" => Some(ClickEvent.suggestCommand(value))
          case "change_page" => value.toIntOption.map(ClickEvent.changePage)
          case "copy_to_clipboard" => Some(ClickEvent.copyToClipboard(value))
          case _ => None
        }

        clickEvent.map(event => (c: Component) => c.clickEvent(event))

      case _ => None
    }

}
