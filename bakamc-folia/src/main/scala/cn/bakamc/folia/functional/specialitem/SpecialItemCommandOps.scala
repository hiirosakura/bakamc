package cn.bakamc.folia.functional.specialitem

import cn.bakamc.common.comp
import cn.bakamc.common.extension.Text.{*, given}
import cn.bakamc.folia.command.dsl.ContextOps.*
import cn.bakamc.folia.database.table.SpecialItem
import cn.bakamc.folia.util.text.ItemExtensions.*
import cn.bakamc.folia.util.text.PluginComponentAdapter.given_ComponentAdapter
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object SpecialItemCommandOps {

  private def manager: SpecialItemManager.type = SpecialItemManager

  def showAll(using ctx: CommandContext[CommandSourceStack]): Unit = {
    val result = manager.getCache.values.mkText(" ")(using { case item: SpecialItem =>
      comp"[${item.id} -> ${item.getItemStack().get.hoveredNameWithCount}]"
    })
    feedback(comp"当前数据库中的特殊物品: $result")
  }

  def give(id: String, count: Option[Int], players: List[Player])(using ctx: CommandContext[CommandSourceStack]): Unit = {
    val itemOpt = manager.getCachedItem(id)
    if (itemOpt.isEmpty) {
      feedback(comp"未找到id为 $id 的特殊物品")
      return
    }
    val item = itemOpt.get
    val c = count.getOrElse(1)
    item.getItemStack(c) match {
      case Some(stack) =>
        players.foreach(p => p.getInventory.addItem(stack))
        if (players.size <= 5) {
          feedback(comp"已向玩家 ${players.mkText(", ")} 授予物品 $item")
        } else {
          feedback(comp"已向 ${players.size} 名玩家授予物品 $item")
        }
      case None =>
        feedback(comp"特殊物品 ${item.id} 无法生成")
    }

  }

  def update(id: Option[String], item: Option[ItemStack])(using ctx: CommandContext[CommandSourceStack]): Unit = {
    //首先获取物品
    val stack = item match {
      //如果存在直接用
      case Some(stack) => stack
      case None if sender.isInstanceOf[Player] =>
        val item = if (!senderPlayer.getInventory.getItemInMainHand.isEmpty) senderPlayer.getInventory.getItemInMainHand
        else senderPlayer.getInventory.getItemInOffHand
        if (item.isEmpty) {
          feedback(comp"没有找到可使用的物品")
          return
        }
        item
      case _ =>
        feedback(comp"没有找到可用的物品")
        return
    }
    //获取id
    val _id = id match {
      case Some(id) => id
      case None => SpecialItem.getItemName(stack)
    }
    val si = SpecialItem(_id, stack)
    manager.update(si) { result =>
      if (result) {
        feedback(comp"已更新特殊物品[$_id] -> $stack")
      }
      else {
        feedback(comp"更新特殊物品失败[$_id]")
      }
    }
  }

  def delete(id: String)(using ctx: CommandContext[CommandSourceStack]): Unit = {
    manager.delete(id) { result =>
      if (result) {
        feedback(comp"已删除特殊物品[$id]")
      }
      else {
        feedback(comp"删除特殊物品失败[$id]")
      }
    }
  }
}
