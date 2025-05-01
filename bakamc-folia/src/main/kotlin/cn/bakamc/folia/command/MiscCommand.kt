package cn.bakamc.folia.command

import cn.bakamc.common.text.bakatext.BakaText
import cn.bakamc.common.text.bakatext.modifier.ColorModifier
import cn.bakamc.common.text.bakatext.modifier.DecorationModifier
import cn.bakamc.common.text.bakatext.modifier.LegacyChatFormattingModifier
import cn.bakamc.common.text.literal
import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.command.base.*
import cn.bakamc.folia.config.MiscConfig.ANVIL_RENAME_DECORATION_MAPPING
import cn.bakamc.folia.config.MiscConfig.ANVIL_RENAME_LEGACY_FORMAT_CHARS
import cn.bakamc.folia.config.MiscConfig.ITEM_RENAME_LENGTH_LIMIT
import cn.bakamc.folia.event.pojo.BlockInfo
import cn.bakamc.folia.extension.onlineDuration
import cn.bakamc.folia.util.launch
import cn.bakamc.folia.util.literalText
import cn.bakamc.folia.util.logger
import cn.bakamc.folia.util.plainText
import moe.forpleuvoir.nebula.common.api.ExperimentalApi
import moe.forpleuvoir.nebula.common.util.defaultLaunch
import moe.forpleuvoir.nebula.serialization.extensions.toSerializeObject
import moe.forpleuvoir.nebula.serialization.json.JsonSerializer.Companion.dumpAsJson
import net.minecraft.network.chat.ClickEvent
import org.bukkit.Chunk
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@OptIn(ExperimentalApi::class)
@Suppress("FunctionName", "DuplicatedCode")
fun MiscCommand(): Command = Command("bakamc") {
    literal("reload") {
        permission("bakamc.admin")
        execute {
            launch {
                BakaMCPlugin.instance.reload()
                it.success("重载配置文件")
            }
        }
    }

    literal("world") {
        permission("bakamc.admin")
        execute<Player> {
            it.feedback(it.sender.world.name)
        }
    }

    "blockInfo" {
        permission("bakamc.admin")
        execute<Player> {
            val block = it.sender.getTargetBlock(null, 5)
            val info = BlockInfo.fromBlock(block)
            it.feedback(info.toSerializeObject().dumpAsJson(false, 2))
        }
    }

    "setPermission" {
        permission("bakamc.admin")
        argument("permission") {
            argument("player") {
                execute { ctx ->
                    val player = ctx.getArg("player") { name -> ctx.sender.server.onlinePlayers.find { it.name == name } }
                    val permission = ctx.getArg("permission")!!
                    player?.addAttachment(BakaMCPlugin.instance)?.setPermission(permission, true)
                }
            }
        }
    }

    "onlineTime" {
        permission("bakamc.admin")
        argument("player") {
            execute {
                val player = it.getArg("player") { name -> it.sender.server.onlinePlayers.find { it.name == name } }
                it.feedback(player?.onlineDuration().toString())
            }
        }
        execute<Player> {
            it.feedback(it.sender.onlineDuration().toString())
        }
    }

    Chunkhot()

    QuickUseCommand()

    "renameItem" {
        permission("bakamc.renameItem")
        argument("name") {
            suggestion {
                val sender = it.sender
                return@suggestion if (sender is Player && !sender.inventory.itemInMainHand.isEmpty) {
                    null
                } else {
                    listOf("§a必须由玩家执行指令,并且手中持有物品")
                }
            }
            execute<Player> { ctx ->
                if (!ctx.sender.inventory.itemInMainHand.isEmpty) {
                    val itemStack = ctx.sender.inventory.itemInMainHand
                    val name = ctx.getArg("name") ?: ""
                    val oldName = itemStack.displayName()
                    val text = BakaText.parse(
                        name, listOf(
                            DecorationModifier(ANVIL_RENAME_DECORATION_MAPPING),
                            ColorModifier,
                            LegacyChatFormattingModifier(formatChars = ANVIL_RENAME_LEGACY_FORMAT_CHARS.toSet())
                        )
                    )
                    if (text.plainText.length > ITEM_RENAME_LENGTH_LIMIT) {
                        ctx.fail("物品名称过长,最大长度:{}", ITEM_RENAME_LENGTH_LIMIT)
                    } else {
//                        ctx.sender.inventory.itemInMainHand.editMeta {
//                            it.displayName(text)
//                        }
                        val new = ItemStack(itemStack)
                        new.editMeta { it.displayName(text) }
                        ctx.sender.inventory.setItemInMainHand(new)
                        ctx.feedback(literal("成功重命名物品: ").append(oldName).append(literal(" -> ")).append(text))
                    }
                } else {
                    ctx.fail("手中必须持有需要重命名的物品")
                }
            }
        }
    }
}

val Chunk.chunkHotAvg: Long
    get() {
        this::class.java.getMethod("getChunkHotAvg").apply {
            return this.invoke(this@chunkHotAvg) as Long
        }
    }


fun CommandNode.Chunkhot(): CommandNode = "chunkhot" {
    permission("bakamc.chunkhot")
    execute { ctx ->
        defaultLaunch {
            runCatching {
                ctx.sender.server.worlds
                    .flatMap { world -> world.loadedChunks.asIterable() }
                    .sortedByDescending { chunk -> chunk.chunkHotAvg }
                    .slice(0 until 10)
                    .forEach { chunk ->
                        val x = chunk.x * 16
                        val z = chunk.z * 16
                        ctx.feedback(
                            literalText("[${chunk.world.name}]chunk hot: ${chunk.chunkHotAvg},")
                                .append(
                                    literalText("点击传送到此区块")
                                        .withStyle {
                                            it.withClickEvent(
                                                ClickEvent(
                                                    ClickEvent.Action.SUGGEST_COMMAND,
                                                    "/execute in ${chunk.world.key.asString()} run tp $x ~ $z"
                                                )
                                            )
                                        }
                                )
                        )
                    }
            }.onFailure {
                logger.error("不支持的服务端,请使用luminol", it)
                ctx.fail("不支持的服务端,请使用luminol")
            }
        }
    }
}