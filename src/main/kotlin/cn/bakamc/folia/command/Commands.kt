package cn.bakamc.folia.command

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
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

    fun item(str: String, item: ItemStack): Component {
        TODO()
    }

    fun player(player: ServerPlayer): Component {
        TODO()
    }

    fun error(error: String = ""): Component {
        TODO()
    }

    fun success(success: String = ""): Component {
        TODO()
    }

}