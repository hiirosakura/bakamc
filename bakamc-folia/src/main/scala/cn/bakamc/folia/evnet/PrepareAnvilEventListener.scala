package cn.bakamc.folia.evnet

import cn.bakamc.folia.BakaMC
import cn.bakamc.folia.config.AnvilTextParserConfig
import cn.bakamc.folia.util.text.plainText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.{EventHandler, EventPriority, Listener}
import org.bukkit.inventory.ItemStack

import scala.language.implicitConversions

object PrepareAnvilEventListener extends Listener {

  @EventHandler(priority = EventPriority.HIGH)
  def onAnvilRename(event: PrepareAnvilEvent): Unit = {
    if (AnvilTextParserConfig.enable) {
      Option(event.getResult).zip(Option(event.getInventory.getResult)).foreach { case (itemStack, result) =>
        val renameText = Option(result.getItemMeta.displayName()).plainText.getOrElse("")
        val text = try {
          val text = AnvilTextParserConfig.parser().parse(renameText)
          BakaMC.logger.debug(s"物品重命名: ${Option(event.getInventory.getFirstItem).map(_.displayName()).plainText} -> $renameText")
          Some(text)
        } catch {
          case e: Throwable =>
            BakaMC.logger.warn(s"物品重命名解析失败: ${Option(event.getInventory.getFirstItem).map(_.displayName()).plainText} -> $renameText")
            BakaMC.logger.warn(e.getMessage, e)
            None
        }
        val newItem = ItemStack(itemStack)
        newItem.editMeta { it =>
          it.displayName(
            text.getOrElse(result.getItemMeta.displayName())
          )
        }
        event.setResult(newItem)
        event.getInventory.setResult(newItem)
      }
    }
  }
}
