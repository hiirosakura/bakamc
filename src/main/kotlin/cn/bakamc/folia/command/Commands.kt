package cn.bakamc.folia.command

import org.bukkit.plugin.java.JavaPlugin

internal fun JavaPlugin.registerCommand() {
    registerCommand(FlyCommand())
    registerCommand(SpecialItemCommand())
}

private fun JavaPlugin.registerCommand(command: BakaCommand) {
    getCommand(command.command)?.apply {
        logger.info("注册指令: /${command.command}")
        setExecutor(command)
        logger.info("注册指令补全: /${command.command}")
        tabCompleter = command
    }
}



