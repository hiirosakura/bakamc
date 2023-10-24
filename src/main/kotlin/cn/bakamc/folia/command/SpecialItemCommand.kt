package cn.bakamc.folia.command

import cn.bakamc.folia.command.SpecialItemCommand.feedback
import cn.bakamc.folia.command.SpecialItemCommand.plus
import cn.bakamc.folia.db.table.SpecialItem
import cn.bakamc.folia.db.table.nameSpace
import cn.bakamc.folia.db.table.toItemStack
import cn.bakamc.folia.db.table.writeNbtTag
import cn.bakamc.folia.item.SpecialItemManager
import cn.bakamc.folia.service.SpecialItemService
import cn.bakamc.folia.util.runNow
import moe.forpleuvoir.nebula.common.util.clamp
import net.minecraft.server.level.ServerPlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack
import org.bukkit.entity.Player

object SpecialItemCommand : BakaCommand {

    override val subCommand: String = "specialitem"

    private val suggestions = mapOf<String, (ServerPlayer, Array<out String>) -> Boolean>(
        "give" to ::give,
        "put" to ::put
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player) {
            if (args.isNullOrEmpty()) {

            }
            return false
        } else {
            sender.sendMessage("§c只有玩家可以使用此命令！")
            return false
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String>? {
        TODO("Not yet implemented")
    }

    private fun give(player: ServerPlayer, args: Array<out String>): Boolean {
        runNow {
            val key = args[1]
            val count = runCatching { args[2].toInt() }.getOrDefault(1).clamp(1, 64)
            val specialItem = SpecialItemService.getItemByKey(key)
            if (specialItem == null) {
                player.feedback(error("特殊物品") + item(key) + error("不存在!"))
            } else {
                specialItem.toItemStack(count)?.apply {
                    player.inventory.add(this)
                    player.feedback(success("已给与玩家") + player(player) + success("物品") + item(this))
                }
            }
        }
        return true
    }

    private fun put(player: ServerPlayer, args: Array<out String>): Boolean {
//        runNow {
//            player.mainHandItem.apply {
//                if (!this.isEmpty) {
//                    val key = kotlin.runCatching { args[2] }.getOrDefault(this.displayName.string)
//                    SpecialItemManager.put(SpecialItem {
//                        this.key = key
//                        this.id = this@apply.nameSpace
//                        this.nbtTag = writeNbtTag(this@apply.tag) ?: ByteArray(0)
//                    }).let {
//                        if (it) {
//                            player.feedback(success("已添加或修改特殊物品") + item(key) + success("为") + item(this))
//                        } else {
//                            player.feedback(error("特殊物品") + item(key) + error("添加失败"))
//                        }
//                    }
//                } else {
//                    player.feedback(error("不能添加空气为特殊物品!"))
//                }
//            }
//        }
        return true
    }

    private fun remove(player: ServerPlayer,args: Array<out String>):Boolean{

        return true
    }
}