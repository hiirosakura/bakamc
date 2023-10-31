package cn.bakamc.folia.command

import cn.bakamc.folia.command.base.*
import cn.bakamc.folia.db.table.SpecialItem
import cn.bakamc.folia.db.table.nameSpace
import cn.bakamc.folia.db.table.toItemStack
import cn.bakamc.folia.db.table.writeNbtTag
import cn.bakamc.folia.item.SpecialItemManager
import cn.bakamc.folia.util.getDisplayNameWithCount
import cn.bakamc.folia.util.launch
import cn.bakamc.folia.util.literalText
import org.bukkit.entity.Player

@Suppress("FunctionName")
internal fun SpecialItemCommand(): Command = Command("specialitem") {
    execute {
        val text = literalText()
        SpecialItemManager.getCache().values.forEach { item ->
            text.append(item.toItemStack(1)!!.getDisplayNameWithCount())
            if (item != SpecialItemManager.getCache().values.last())
                text.append(", ")
        }
        it.feedback("当前的数据库中特殊物品:[{}]", text)
    }
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
            ctx.fail("特殊物品[{}]不存在!", key)
        } else {
            specialItem.toItemStack(count)?.apply {
                ctx.success("已给予玩家{}物品{}", player, this)
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
                        ctx.success("已添加或修改特殊物品[{}]为{}", key, this@apply)
                    } else {
                        ctx.fail("特殊物品[{}]添加失败!", key)
                    }
                }
            }
        } else {
            ctx.fail("不能添加空气为特殊物品,请手持物品!")
        }
    }
}

internal val remove: (CommandContext<out Player>) -> Unit = { ctx ->
    val key = ctx.getArg("key")!!
    launch {
        SpecialItemManager.remove(key)?.let {
            ctx.success("已删除特殊物品[{}]", key)
        } ?: ctx.fail("特殊物品[{}]不存在!", key)
    }
}

