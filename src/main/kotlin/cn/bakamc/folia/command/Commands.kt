package cn.bakamc.folia.command

import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

internal fun JavaPlugin.registerCommand() {
    registerCommand(FlyCommand)
    registerCommand(SpecialItemCommand)


}

private fun JavaPlugin.registerCommand(command: BakaCommand) {
    getCommand(command.command)?.apply {
        setExecutor(command)
        tabCompleter = command
    }
}


internal interface BakaCommand : CommandExecutor, TabCompleter {

    val subCommand: String

    val command: String get() = "baka-$subCommand"

}