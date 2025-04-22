package cn.bakamc.folia.command

import cn.bakamc.folia.command.base.Command
import cn.bakamc.folia.command.base.execute
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

internal fun JavaPlugin.registerBakamcCommand() {
    registerCommand(FlyCommand())
    registerCommand(SpecialItemCommand())
    registerCommand(MiscCommand())
    registerCommand(Command("end") {
        execute<Player> { ctx ->
            ctx.sender.openInventory(ctx.sender.enderChest)
        }
    })
}

private fun JavaPlugin.registerCommand(command: Command) {
    getCommand(command.command)?.apply {
        logger.info("注册指令: /${command.command}")
        setExecutor(command)
        logger.info("注册指令补全: /${command.command}")
        tabCompleter = command
    }
}



