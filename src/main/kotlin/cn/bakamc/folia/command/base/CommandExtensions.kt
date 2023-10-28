package cn.bakamc.folia.command.base

import cn.bakamc.folia.util.getDisplayNameWithCount
import cn.bakamc.folia.util.literalText
import cn.bakamc.folia.util.toServerPlayer
import cn.bakamc.folia.util.wrapInSquareBrackets
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


fun command(command: String, scope: Command.() -> Unit): Command {
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
        scope(this)
    }
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
            it.sender.feedback(literalText("§c只有${T::class.java.simpleName}可以使用此命令!"))
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
            it.startsWith(str)
            add(it)
        }
    }
}

fun CommandSender.feedback(message: Component) {
    when (this) {
        is Player -> (this as? Player)?.toServerPlayer()!!.sendSystemMessage(message)
        else      -> sendMessage(message.string)
    }
}

fun item(item: ItemStack): MutableComponent {
    return item.getDisplayNameWithCount()
}


fun item(item: String, count: Int = 0): MutableComponent {
    return wrapInSquareBrackets(literalText(item + if (count == 0) "" else " x$count")).withStyle {
        it.applyFormat(ChatFormatting.GOLD)
    }
}

fun player(player: ServerPlayer): MutableComponent {
    return wrapInSquareBrackets(literalText(player.displayName)).withStyle {
        it.applyFormat(ChatFormatting.AQUA)
    }
}

fun error(message: String = ""): MutableComponent {
    return literalText(message).withStyle {
        it.applyFormat(ChatFormatting.RED)
    }
}

fun tip(message: String = ""): MutableComponent {
    return literalText(message).withStyle {
        it.applyFormat(ChatFormatting.GOLD)
    }
}

fun success(message: String = ""): MutableComponent {
    return literalText(message).withStyle {
        it.applyFormat(ChatFormatting.GREEN)
    }
}

fun format(message: String, formatting: ChatFormatting): MutableComponent {
    return literalText(message).withStyle {
        it.applyFormat(formatting)
    }
}

operator fun MutableComponent.plus(component: MutableComponent): MutableComponent {
    return this.append(component)
}

fun ServerPlayer.feedback(message: String) {
    this.sendSystemMessage(literalText(message))
}

fun ServerPlayer.feedback(vararg message: MutableComponent) {
    when {
        message.size == 1 -> this.sendSystemMessage(message[0])
        message.isEmpty() -> return
        message.size >= 2 -> {
            val msg = literalText()
            message.forEach {
                msg.append(it)
            }
            this.sendSystemMessage(msg)
        }
    }
}