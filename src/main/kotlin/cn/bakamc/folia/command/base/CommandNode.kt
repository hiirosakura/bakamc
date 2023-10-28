package cn.bakamc.folia.command.base

import cn.bakamc.folia.util.literalText
import org.bukkit.command.CommandSender

abstract class CommandNode {

    abstract val command: String

    /**
     * 子命令类型只能为一种
     * 如果是[CommandSubNode.Type.ARGUMENT]类型，则[subNodes]只能包含一个子指令
     */
    protected abstract val subNodes: MutableList<CommandSubNode>

    abstract var permission: (CommandContext<out CommandSender>) -> Boolean

    abstract var executor: ((CommandContext<out CommandSender>) -> Unit)?

    open var suggestions: ((CommandContext<out CommandSender>) -> List<String>?)? = func@{
        if (subNodes.isNotEmpty()) {
            //找到第一个节点，判断是否为[CommandSubNode.Type.ARGUMENT]类型，如果是则直接获取此节点的[suggestions]
            //如果是[CommandSubNode.Type.LITERAL]类型，则将所有节点指令名作为候选词
            subNodes.first().let { node ->
                return@func if (node.type == CommandSubNode.Type.ARGUMENT) {
                    node.suggestions?.invoke(it)
                } else subNodes.map { it.command }
            }
        }
        null
    }

    open fun onCommand(context: CommandContext<out CommandSender>) {
        if (!permission(context)) {
            context.feedback(literalText("§c你没有权限执行该指令"))
            return
        }
        context.isEnd {
            runCatching {
                executor?.let { it(this) } ?: this.feedback("§c无法执行该指令:${this.commandLine}")
            }.onFailure {
                throw CommandExecuteException(it)
            }
        }.hasNext { next ->
            runCatching {
                subNode(next)!!
            }.onFailure { throwable ->
                if (throwable is NullPointerException) context.feedback("§c未知指令:${context.commandLine} $next")
                else throw throwable
            }.onSuccess { node ->
                node.onCommand(this.next(this@CommandNode))
            }
        }
    }

    open fun onTabComplete(context: CommandContext<out CommandSender>): List<String>? {
        if (!permission(context)) {
            return listOf("§c你没有权限执行该指令")
        }

        context.isEnd {
            return runCatching {
                suggestions?.let { it(this) }
            }.getOrNull()
        }.hasNext { next ->
            runCatching {
                subNode(next)!!
            }.onFailure { throwable ->
                if (throwable is NullPointerException) {
                    return suggestions?.invoke(context)
                } else throw throwable
            }.onSuccess { node ->
                return node.onTabComplete(this.next(this@CommandNode))
            }
        }
        return listOf("§c没有更多东西了")
    }

    protected open fun subNode(command: String): CommandSubNode? {
        if (subNodes.isNotEmpty()) {
            subNodes.first().let { node ->
                if (node.type == CommandSubNode.Type.ARGUMENT) {
                    return node
                } else return subNodes.find { it.command == command }
            }
        }
        return null
    }

    fun append(node: CommandSubNode) {
        subNodes.ifEmpty {
            subNodes.add(node)
            return
        }
        if (subNodes.first().type == CommandSubNode.Type.ARGUMENT || node.type == CommandSubNode.Type.ARGUMENT) {
            throw IllegalArgumentException("(${this.command})子指令为[ARGUMENT]类型时只能有一个子指令。")
        } else {
            subNodes.add(node)
        }
    }

}

