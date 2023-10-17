package cn.bakamc.folia.command

import org.bukkit.plugin.java.JavaPlugin

fun JavaPlugin.registerCommand() {
    getCommand(FlyCommand.CMD)?.apply {
        setExecutor(FlyCommand)
        tabCompleter = FlyCommand
    }


}