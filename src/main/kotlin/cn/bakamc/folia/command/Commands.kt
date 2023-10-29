package cn.bakamc.folia.command

import cn.bakamc.folia.command.base.Command
import org.bukkit.plugin.java.JavaPlugin

internal fun JavaPlugin.registerCommand() {
    registerCommand(FlyCommand())
    registerCommand(SpecialItemCommand())
    registerCommand(MiscCommand())
}

private fun JavaPlugin.registerCommand(command: Command) {
    getCommand(command.command)?.apply {
        logger.info("注册指令: /${command.command}")
        setExecutor(command)
        logger.info("注册指令补全: /${command.command}")
        tabCompleter = command
    }
}



