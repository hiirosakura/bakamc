package cn.bakamc.folia.command

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
        "give" to { player, args ->
            runNow {
                val key = args[1]
                val count = runCatching { args[2].toInt() }.getOrDefault(1).clamp(1, 64)
                val specialItem = SpecialItemService.getItemByKey(key)
                if (specialItem == null) {

                    player.sendSystemMessage(error("特殊物品${item(key)}§c不存在"))
                } else {
                    CraftItemStack.asBukkitCopy(specialItem.toItemStack(count)).apply {
                        player.inventory.addItem(this)
                        player.sendMessage("§a已给予§d[${player.name}]§a物品§6[${specialItem.id}×${this.amount}]")
                    }
                }
            }
            true
        },
        "put" to { player, args ->
            runNow {
                CraftItemStack.asNMSCopy(player.inventory.itemInMainHand).apply {
                    if (!this.isEmpty) {
                        val key = kotlin.runCatching { args[2] }.getOrDefault(this.displayName.string)
                        SpecialItemManager.put(SpecialItem {
                            this.key = key
                            this.id = this@apply.nameSpace
                            this.nbtTag = writeNbtTag(this@apply.tag) ?: ByteArray(0)
                        }).let {
                            if (it) {
                                player.sendMessage("§a特殊物品§6[$key]§a已添加")
                            } else {
                                player.sendMessage("§c特殊物品§6[$key]§c添加失败")
                            }
                        }
                    } else {
                        player.sendMessage("§c不能添加空气为特殊物品!")
                    }
                }
            }
            true
        }
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

}