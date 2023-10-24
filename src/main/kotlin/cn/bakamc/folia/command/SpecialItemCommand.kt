package cn.bakamc.folia.command

import cn.bakamc.folia.db.table.SpecialItem
import cn.bakamc.folia.db.table.nameSpace
import cn.bakamc.folia.db.table.toItemStack
import cn.bakamc.folia.db.table.writeNbtTag
import cn.bakamc.folia.item.SpecialItemManager
import cn.bakamc.folia.service.SpecialItemService
import cn.bakamc.folia.util.launch
import moe.forpleuvoir.nebula.common.util.clamp
import net.minecraft.server.level.ServerPlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object SpecialItemCommand : BakaCommand {

    override val subCommand: String = "specialitem"

    private val suggestions = mapOf<String, (ServerPlayer, Array<out String>) -> Boolean>(
        "give" to ::give,
        "put" to ::put,
        "remove" to ::remove
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is ServerPlayer) {
            if (args.isNullOrEmpty()) {
                sender.feedback(error("错误的指令格式!") + success("/baka-specialitem <operate> <key>"))
                return false
            } else if (args.size == 1 && args[0] == "put") {
                suggestions["put"]!!.invoke(sender, args)
            } else if (args.size >= 2) {
                suggestions[args[1]]?.invoke(sender, args) ?: {
                    sender.feedback(error("错误的指令格式!") + success("/baka-specialitem <operate> <key>"))
                }
            }
            return true
        } else {
            sender.sendMessage("§c只有玩家可以使用此命令！")
            return false
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String>? {
        if (args.isNullOrEmpty()) return null
        if (args.size == 1) {
            return suggestions.keys.toMutableList()
        } else if (args.size == 2) {
            return when (args[0]) {
                "give", "remove" -> SpecialItemManager.getCache().keys.toMutableList()
                else             -> null
            }
        }
        return null
    }

    private fun give(player: ServerPlayer, args: Array<out String>): Boolean {
        val key = args[1]
        val count = runCatching { args[2].toInt() }.getOrDefault(1).clamp(1, 64)
        launch {
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
        player.mainHandItem.apply {
            if (!this.isEmpty) {
                val key = kotlin.runCatching { args[2] }.getOrDefault(this.displayName.string)
                launch {
                    SpecialItemManager.put(SpecialItem {
                        this.key = key
                        this.id = this@apply.nameSpace
                        this.nbtTag = writeNbtTag(this@apply.tag) ?: ByteArray(0)
                    }).let {
                        if (it) {
                            player.feedback(success("已添加或修改特殊物品") + item(key) + success("为") + item(this@apply))
                        } else {
                            player.feedback(error("特殊物品") + item(key) + error("添加失败"))
                        }
                    }
                }
            } else {
                player.feedback(error("不能添加空气为特殊物品!"))
            }
        }
        return true
    }

    private fun remove(player: ServerPlayer, args: Array<out String>): Boolean {
        if (args.size != 2) {
            player.feedback(error("错误的指令格式!") + success("/baka-specialitem remove <key>"))
            return false
        }
        val key = args[1]
        launch {
            SpecialItemManager.remove(key)?.let {
                player.feedback(success("已删除特殊物品") + item(key))
            } ?: player.feedback(error("特殊物品") + item(key) + error("不存在!"))
        }
        return true
    }
}