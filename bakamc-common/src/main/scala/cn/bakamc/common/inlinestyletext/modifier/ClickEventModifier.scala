package cn.bakamc.common.inlinestyletext.modifier

import cn.bakamc.common.inlinestyletext.TextModifier
import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializePrimitive}
import moe.forpleuvoir.nebula.serialization.codec.Codec
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent

import java.util.concurrent.ConcurrentHashMap
import scala.util.Try

object ClickEventModifier extends TextModifier {

  val CODEC: Codec[ClickEventModifier.type] = new Codec[ClickEventModifier.type] {
    override def deserialization(data: SerializeElement): Try[ClickEventModifier.type] = Try(ClickEventModifier)

    override def serialization(value: ClickEventModifier.type): SerializeElement =
      SerializePrimitive("Unit")
  }

  given Codec[ClickEventModifier.type] = ClickEventModifier.CODEC

  // 使用正则提取组直接捕获 action 和 value
  private final val ClickPattern = """c:(open_url|open_file|run_command|suggest_command|change_page|copy_to_clipboard)=>(.+)""".r

  override def modify(exp: String, origin: Component): Option[Component] =
    exp match {
      case e if e == "c:none" => Some(origin.clickEvent(null))
      case ClickPattern(action, value) =>
        val clickEvent: Option[ClickEvent] = action match {
          case "open_url" => Some(ClickEvent.openUrl(value))
          case "open_file" => Some(ClickEvent.openFile(value))
          case "run_command" => Some(ClickEvent.runCommand(value))
          case "suggest_command" => Some(ClickEvent.suggestCommand(value))
          case "change_page" => value.nn.toIntOption.map(ClickEvent.changePage)
          case "copy_to_clipboard" => Some(ClickEvent.copyToClipboard(value))
          case _ => None
        }
        clickEvent.map(event => origin.clickEvent(event))
      case _ => None
    }

}
