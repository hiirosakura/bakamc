package cn.bakamc.folia.command

import cn.bakamc.folia.db.table.SpecialItem
import cn.bakamc.folia.db.table.nameSpace
import cn.bakamc.folia.db.table.toItemStack
import cn.bakamc.folia.db.table.writeNbtTag
import cn.bakamc.folia.item.SpecialItemManager
import cn.bakamc.folia.util.launch
import cn.bakamc.folia.util.toServerPlayer
import moe.forpleuvoir.nebula.common.util.clamp
import net.minecraft.server.level.ServerPlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object SpecialItemCommand : BakaCommand {

    override val command: String = "specialitem"

    private val suggestions = mapOf<String, (ServerPlayer, Array<out String>) -> Unit>(
        "give" to ::give,
        "put" to ::put,
        "remove" to ::remove
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val player = sender.toServerPlayer()!!
            if (args.isNullOrEmpty()) {
                player.feedback(error("错误的指令格式!") + success("/specialitem <operate> <key>"))
            } else if (args.size == 1 && args[0] == "put") {
                suggestions["put"]!!.invoke(player, args)
            } else if (args.size >= 2) {
                suggestions[args[0]]?.invoke(player, args) ?: {
                    player.feedback(error("错误的指令格式!") + success("/specialitem <operate> <key>"))
                }
            }
        } else {
            sender.sendMessage("§c只有玩家可以使用此命令！")
        }
        return true
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

    private fun give(player: ServerPlayer, args: Array<out String>) {
        val key = args[1]
        val count = runCatching { args[2].toInt() }.getOrDefault(1).clamp(1, 64)
        launch {
            val specialItem = SpecialItemManager.getCachedItem(key)
            if (specialItem == null) {
                player.feedback(error("特殊物品") + item(key) + error("不存在!"))
            } else {
                specialItem.toItemStack(count)?.apply {
                    player.feedback(success("已给与玩家") + player(player) + success("物品") + item(this))
                    player.inventory.add(this)
                }
            }
        }
    }

    private fun put(player: ServerPlayer, args: Array<out String>) {
        player.mainHandItem.apply {
            if (!this.isEmpty) {
                val key = runCatching { args[1] }.getOrDefault(this.hoverName.string)
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
    }

    private fun remove(player: ServerPlayer, args: Array<out String>) {
        if (args.size != 2) {
            player.feedback(error("错误的指令格式!") + success("/specialitem remove <key>"))
            return
        }
        val key = args[1]
        launch {
            SpecialItemManager.remove(key)?.let {
                player.feedback(success("已删除特殊物品") + item(key))
            } ?: player.feedback(error("特殊物品") + item(key) + error("不存在!"))
        }
    }
}