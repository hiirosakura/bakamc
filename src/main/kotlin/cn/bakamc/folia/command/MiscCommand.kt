package cn.bakamc.folia.command

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.command.base.Command
import cn.bakamc.folia.command.base.execute
import cn.bakamc.folia.command.base.literal
import cn.bakamc.folia.util.launch
import cn.bakamc.folia.util.literalText
import cn.bakamc.folia.util.withColor
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import org.bukkit.entity.Player

@Suppress("FunctionName", "DuplicatedCode")
fun MiscCommand(): Command = Command("bakamc") {
    literal("reload") {
        execute {
            launch {
                BakaMCPlugin.instance.reload()
                it.success("重载配置文件")
            }
        }
    }
    literal("world") {
        execute<Player> {
            val text = literalText(it.sender.world.key.toString())
            text.withStyle { style ->
                style.withColor(Colors.GREEN_APPLE)
                style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy to clipboard")))
                style.withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, it.sender.world.key.toString()))
            }
            it.feedback(text)
        }
    }
    literal("biome") {
        execute<Player> {
            val biome = it.sender.world.getBiome(it.sender.location).key.toString()
            val text = literalText(biome)
            text.withStyle { style ->
                style.withColor(Colors.GREEN_APPLE)
                style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy to clipboard")))
                style.withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, biome))
            }
            it.feedback(text)
        }
    }
    literal("block") {
        execute<Player> {
            val block = it.sender.getTargetBlock(null, 5)
            val blockKey = block.blockData.material.key.toString()
            val text = literalText(blockKey)
            text.withStyle { style ->
                style.withColor(Colors.GREEN_APPLE)
                style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy to clipboard")))
                style.withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, blockKey))
            }
            it.feedback(text)
        }
    }
}

