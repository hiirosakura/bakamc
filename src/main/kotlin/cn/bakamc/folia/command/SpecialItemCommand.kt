package cn.bakamc.folia.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

object SpecialItemCommand: BakaCommand {

    override val subCommand: String = "specialitem"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String>? {
        TODO("Not yet implemented")
    }

}