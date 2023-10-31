package cn.bakamc.folia.command.base

import cn.bakamc.folia.util.Style
import cn.bakamc.folia.util.formatText
import cn.bakamc.folia.util.logger
import cn.bakamc.folia.util.sendMessage
import net.minecraft.ChatFormatting
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class Command(
    override val command: String,
) : CommandNode(), CommandExecutor, TabCompleter {

    override val subNodes: MutableList<CommandSubNode> = mutableListOf()

    override var permission: (CommandContext<out CommandSender>) -> Boolean = { true }

    override var executor: ((CommandContext<out CommandSender>) -> Unit)? = null

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        runCatching {
            onCommand(CommandContext(sender, command.name, args.toList()).next(this))
        }.onFailure {
            if (it is CommandExecuteException) sender.sendMessage(formatText("执行指令({})时出现异常!请联系服务器管理员!", Style(ChatFormatting.RED), command.name))
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