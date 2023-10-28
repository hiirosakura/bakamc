package cn.bakamc.folia.command

import cn.bakamc.folia.command.base.*
import cn.bakamc.folia.db.table.SpecialItem
import cn.bakamc.folia.db.table.nameSpace
import cn.bakamc.folia.db.table.toItemStack
import cn.bakamc.folia.db.table.writeNbtTag
import cn.bakamc.folia.item.SpecialItemManager
import cn.bakamc.folia.util.launch
import org.bukkit.entity.Player

@Suppress("FunctionName")
internal fun SpecialItemCommand(): Command = command("specialitem") {
    literal("give") {
        argument("key") {
            suggestion { SpecialItemManager.getCache().keys.toList() }
            execute<Player>(give)
            argument("count") {
                execute<Player>(give)
            }
        }
    }
    literal("put") {
        argument("key") {
            execute(put)
        }
        execute(put)
    }
    literal("remove") {
        argument("key") {
            suggestion { SpecialItemManager.getCache().keys.toList() }
            execute(remove)
        }
    }
}

internal val give: (CommandContext<out Player>) -> Unit = { ctx ->
    val player = ctx.player!!
    val key = ctx.getArg("key")!!
    val count = (ctx.getArg("count")?.toInt() ?: 1).coerceAtLeast(1)
    launch {
        val specialItem = SpecialItemManager.getCachedItem(key)
        if (specialItem == null) {
            ctx.feedback(error("特殊物品") + item(key) + error("不存在!"))
        } else {
            specialItem.toItemStack(count)?.apply {
                ctx.feedback(success("已给与玩家") + player(player) + success("物品") + item(this))
                player.inventory.add(this)
            }
        }
    }
}

internal val put: (CommandContext<out Player>) -> Unit = { ctx ->
    ctx.player!!.mainHandItem.apply {
        if (!this.isEmpty) {
            val key = ctx.getArg("key") ?: this.hoverName.string
            launch {
                SpecialItemManager.put(SpecialItem {
                    this.key = key
                    this.id = this@apply.nameSpace
                    this.nbtTag = writeNbtTag(this@apply.tag) ?: ByteArray(0)
                }).let {
                    if (it) {
                        ctx.feedback(
                            success("已添加或修改特殊物品") + item(key) + success("为") + item(
                                this@apply
                            )
                        )
                    } else {
                        ctx.feedback(error("特殊物品") + item(key) + error("添加失败"))
                    }
                }
            }
        } else {
            ctx.feedback(error("不能添加空气为特殊物品!"))
        }
    }
}

internal val remove: (CommandContext<out Player>) -> Unit = { ctx ->
    val key = ctx.getArg("key")!!
    launch {
        SpecialItemManager.remove(key)?.let {
            ctx.feedback(success("已删除特殊物品") + item(key))
        } ?: ctx.feedback(error("特殊物品") + item(key) + error("不存在!"))
    }
}

