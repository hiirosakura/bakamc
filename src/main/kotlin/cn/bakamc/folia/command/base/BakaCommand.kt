package cn.bakamc.folia.command.base

import cn.bakamc.folia.util.literalText
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class BakaCommand(
    override val command: String,
) : BakaCommandNode, CommandExecutor, TabCompleter {

    override val subNodes: MutableList<BakaCommandSubNode> = mutableListOf()

    override var permission: (BakaCommandContext) -> Boolean = { it.sender.hasPermission(command) }

    override var executor: ((BakaCommandContext) -> Unit)? = null

    override var suggestions: ((BakaCommandContext) -> List<String>?)? = null

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        onCommand(BakaCommandContext(sender, command.name, this), args)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String>? {
        return onTabComplete(BakaCommandContext(sender, command.name, this), args)
    }

    override fun onCommand(context: BakaCommandContext, args: Array<out String>) {
        if (permission(context)) {
            context.feedback(literalText("§c你没有权限执行该指令"))
            return
        }

    }

    override fun onTabComplete(context: BakaCommandContext, args: Array<out String>): List<String>? {
        if (permission(context)) {
            return listOf("§c你没有权限执行该指令")
        }
        return null
    }

}