package cn.bakamc.folia.command

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.command.base.*
import cn.bakamc.folia.event.pojo.BlockInfo
import cn.bakamc.folia.util.launch
import cn.bakamc.folia.util.literalText
import cn.bakamc.folia.util.logger
import moe.forpleuvoir.nebula.common.api.ExperimentalApi
import moe.forpleuvoir.nebula.common.util.defaultLaunch
import moe.forpleuvoir.nebula.serialization.extensions.toSerializeObject
import moe.forpleuvoir.nebula.serialization.json.JsonSerializer.Companion.dumpAsJson
import net.minecraft.network.chat.ClickEvent
import org.bukkit.Chunk
import org.bukkit.entity.Player

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

    Chunkhot()

    QuickUseCommand()

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