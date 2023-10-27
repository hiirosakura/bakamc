package cn.bakamc.folia.command

import cn.bakamc.folia.command.base.BakaCommandNode
import org.bukkit.plugin.java.JavaPlugin

internal fun JavaPlugin.registerCommand() {
    registerCommand(FlyCommand())
    registerCommand(SpecialItemCommand())
}

private fun JavaPlugin.registerCommand(command: BakaCommandNode) {
    getCommand(command.command)?.apply {
        logger.info("注册指令: /${command.command}")
        setExecutor(command)
        logger.info("注册指令补全: /${command.command}")
        tabCompleter = command
    }
}



