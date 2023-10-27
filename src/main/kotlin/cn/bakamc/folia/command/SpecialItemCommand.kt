package cn.bakamc.folia.command

import cn.bakamc.folia.command.base.*
import cn.bakamc.folia.command.base.BakaCommandNode
import cn.bakamc.folia.command.base.argument
import cn.bakamc.folia.command.base.literal
import cn.bakamc.folia.command.base.root
import cn.bakamc.folia.db.table.SpecialItem
import cn.bakamc.folia.db.table.nameSpace
import cn.bakamc.folia.db.table.toItemStack
import cn.bakamc.folia.db.table.writeNbtTag
import cn.bakamc.folia.item.SpecialItemManager
import cn.bakamc.folia.util.launch
import cn.bakamc.folia.util.toServerPlayer
import moe.forpleuvoir.nebula.common.util.clamp
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("FunctionName")
internal fun SpecialItemCommand(): BakaCommandNode = root("specialitem") {
    literal("give") {
        argument("key") {
            suggestion { SpecialItemManager.getCache().keys.toList() }
            execute(give)
            argument("count") {
                execute(give)
            }
        }
    }
    literal("put") {
        argument("key") {
            execute(put)
        }
        execute(put)
    }
    literal("remove"){
        argument("key") {
            suggestion { SpecialItemManager.getCache().keys.toList() }
            execute(remove)
        }
    }
}

internal val give: BakaCommandNode.(sender: CommandSender) -> Unit = { sender ->
    val player = (sender as Player).toServerPlayer()!!
    val key = getArg("key")!!
    val count = (getArg("count")?.toInt() ?: 1).clamp(1, 64)
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

internal val put: BakaCommandNode.(sender: CommandSender) -> Unit = { sender ->
    val player = (sender as Player).toServerPlayer()!!
    player.mainHandItem.apply {
        if (!this.isEmpty) {
            val key = getArg("key") ?: this.hoverName.string
            launch {
                SpecialItemManager.put(SpecialItem {
                    this.key = key
                    this.id = this@apply.nameSpace
                    this.nbtTag = writeNbtTag(this@apply.tag) ?: ByteArray(0)
                }).let {
                    if (it) {
                        player.feedback(
                            success("已添加或修改特殊物品") + item(key) + success("为") + item(
                                this@apply
                            )
                        )
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

internal val remove: BakaCommandNode.(sender: CommandSender) -> Unit = { sender ->
    val player = (sender as Player).toServerPlayer()!!
    if (args.size != 2) {
        player.feedback(error("错误的指令格式!") + success("/specialitem remove <key>"))
    }
    val key = getArg("key")!!
    launch {
        SpecialItemManager.remove(key)?.let {
            player.feedback(success("已删除特殊物品") + item(key))
        } ?: player.feedback(error("特殊物品") + item(key) + error("不存在!"))
    }
}

