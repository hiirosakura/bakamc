package cn.bakamc.folia.command

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.command.base.Command
import cn.bakamc.folia.command.base.execute
import cn.bakamc.folia.command.base.literal
import cn.bakamc.folia.util.launch
import org.bukkit.entity.Player

@Suppress("FunctionName")
fun MiscCommand(): Command = Command("bakamc") {
    literal("reload") {
        execute {
            launch {
                BakaMCPlugin.instance.reload()
                it.feedback("§a重载配置文件")
            }
        }
    }
    literal("world") {
        execute<Player> {
            it.feedback("§a${it.sender.world.key}")
        }
    }
    literal("biome") {
        execute<Player> {
            it.feedback("§a${it.sender.world.getBiome(it.sender.location).key}")
        }
    }
    literal("block") {
        execute<Player> {
            val block = it.sender.getTargetBlock(null, 5)
            it.feedback("§a${block.blockData.material.key}")
        }
    }
}

