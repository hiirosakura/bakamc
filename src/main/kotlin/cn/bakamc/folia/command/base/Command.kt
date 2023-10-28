package cn.bakamc.folia.command.base

import cn.bakamc.folia.util.logger
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class Command(
    override val command: String,
) : CommandNode(), CommandExecutor, TabCompleter {

    override val subNodes: MutableList<CommandSubNode> = mutableListOf()

    override var permission: (CommandContext<out CommandSender>) -> Boolean = { it.sender.hasPermission(command) }

    override var executor: ((CommandContext<out CommandSender>) -> Unit)? = null

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        runCatching {
            onCommand(CommandContext(sender, command.name, args.toList()).next(this))
        }.onFailure {
            if (it is CommandExecuteException) sender.sendMessage("§c执行指令(${command.name})时出现异常!请联系服务器管理员!")
            logger.error("执行指令(${command.name})时出现异常", it)
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String>? {
        return runCatching {
            onTabComplete(CommandContext(sender, command.name, args.toList()).next(this))
        }.onFailure {
            logger.error("指令补全时出现异常", it)
        }.getOrNull()
    }

}