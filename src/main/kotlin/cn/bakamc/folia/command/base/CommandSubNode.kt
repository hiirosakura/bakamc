package cn.bakamc.folia.command.base

import org.bukkit.command.CommandSender

abstract class CommandSubNode(
    override val command: String,
    val parent: CommandNode
) : CommandNode() {

    enum class Type {
        LITERAL,
        ARGUMENT
    }

    abstract val type: Type

    override val subNodes: MutableList<CommandSubNode> = mutableListOf()

    override var permission: (CommandContext<out CommandSender>) -> Boolean = { it.sender.hasPermission(command) }

    override var executor: ((CommandContext<out CommandSender>) -> Unit)? = null

    override fun onCommand(context: CommandContext<out CommandSender>) {
        if (type == Type.ARGUMENT) {
            context.putArg(command, context.currentNodeString())
        }
        super.onCommand(context)
    }

    override fun onTabComplete(context: CommandContext<out CommandSender>): List<String>? {
        if (type == Type.ARGUMENT) {
            context.putArg(command, context.currentNodeString())
        }
        return super.onTabComplete(context)
    }

}