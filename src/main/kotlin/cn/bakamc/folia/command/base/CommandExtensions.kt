package cn.bakamc.folia.command.base

import cn.bakamc.folia.util.toServerPlayer
import net.minecraft.network.chat.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


fun Command(command: String, scope: Command.() -> Unit): Command {
    return Command(command).apply(scope)
}

fun CommandNode.literal(command: String, scope: CommandNode.() -> Unit): CommandNode {
    return LiteralCommandSubNode(command, this).apply {
        parent.append(this)
        scope(this)
    }
}

fun CommandNode.argument(command: String, scope: CommandNode.() -> Unit): CommandNode {
    return ArgumentCommandSubNode(command, this).apply {
        parent.append(this)
        this.suggestions = {
            listOf("<${this.command}>")
        }
        scope(this)
    }
}

fun CommandNode.permission(permission: (CommandContext<out CommandSender>) -> Boolean): CommandNode {
    this.permission = permission
    return this
}

fun CommandNode.execute(executor: (CommandContext<out CommandSender>) -> Unit): CommandNode {
    this.executor = executor
    return this
}

@JvmName("executeSenderTyped")
@Suppress("UNCHECKED_CAST")
inline fun <reified T : CommandSender> CommandNode.execute(noinline executor: (CommandContext<out T>) -> Unit): CommandNode {
    this.executor = executor@{
        if (it.sender !is T) {
            it.fail("只有[{}]可以使用此命令!",T::class.java.simpleName)
            return@executor
        }
        executor.invoke(it as CommandContext<out T>)
    }
    return this
}

fun CommandNode.suggestion(candidates: (CommandContext<out CommandSender>) -> List<String>?): CommandNode {
    this.suggestions = {
        shouldSuggestion(it.building, candidates(it))
    }
    return this
}

fun CommandNode.suggestionBuild(candidates: (CommandContext<out CommandSender>, String) -> List<String>?): CommandNode {
    this.suggestions = {
        candidates.invoke(it, it.building)
    }
    return this
}

fun shouldSuggestion(str: String, candidates: List<String>?): List<String> {
    return buildList {
        candidates?.forEach {
            if (it.startsWith(str)) {
                add(it)
            }
        }
    }
}

fun CommandSender.feedback(message: Component) {
    when (this) {
        is Player -> (this as? Player)?.toServerPlayer()!!.sendSystemMessage(message)
        else      -> sendMessage(message.string)
    }
}

