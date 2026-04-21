package cn.bakamc.folia.command

import cn.bakamc.folia.command.dsl.ArgumentScope.*
import cn.bakamc.folia.command.dsl.RootCommand
import cn.bakamc.folia.functional.specialitem.{SpecialItemCommandOps, SpecialItemManager}
import com.mojang.brigadier.arguments.{IntegerArgumentType, StringArgumentType}
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import org.bukkit.inventory.ItemStack

object SpecialItemCommand {

  private def ops = SpecialItemCommandOps

  private def manager = SpecialItemManager

  val root: RootCommand = RootCommand("special_item") {
    requires("bkamc.special_item")
    //直接执行获取缓存中的所有特殊物品
    execute(ops.showAll)

    "give" {
      argument("id", StringArgumentType.string()) {
        suggests {
          manager.getCache.keys.toList
        }
        //只有id的分支
        execute {
          val id = getArg[String]("id")
          val player = senderPlayer
          ops.give(id, None, List(player))
        }
        argument("count", IntegerArgumentType.integer(1)) {
          //id和数量
          execute {
            val id = getArg[String]("id")
            val count = getArg[Int]("count")
            val player = senderPlayer
            ops.give(id, Some(count), List(player))
          }
          argument("player", ArgumentTypes.players()) {
            //id、数量和玩家
            execute {
              val id = getArg[String]("id")
              val count = getArg[Int]("count")
              val player = getPlayers("player")
              ops.give(id, Some(count), player)
            }
          }
        }
      }
    }

    "update" {
      execute(ops.update(None, None))
      argument("id", StringArgumentType.string()) {
        execute {
          ops.update(Some(getArg[String]("id")), None)
        }
        argument("item", ArgumentTypes.itemStack()) {
          execute{
            ops.update(Some(getArg[String]("id")), Some(getArg[ItemStack]("item")))
          }
        }
      }
    }

    "delete" {
      argument("id", StringArgumentType.string()) {
        suggests {
          manager.getCache.keys.toList
        }
        execute(ops.delete(getArg[String]("id")))
      }
    }

  }

}
