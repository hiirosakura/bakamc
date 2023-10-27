package cn.bakamc.folia.command.base

import cn.bakamc.folia.util.getDisplayNameWithCount
import cn.bakamc.folia.util.literalText
import cn.bakamc.folia.util.wrapInSquareBrackets
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import org.bukkit.command.CommandSender


internal fun root(command: String, scope: BakaCommandNode.() -> Unit): BakaCommandNode {
    return object : BakaCommandNode {

        override val command: String = command

        override val root: String = command

        override val type: BakaCommandNode.Type = BakaCommandNode.Type.LITERAL

        override var permission: (CommandSender) -> Boolean = { it.hasPermission(command) }

        override val subNodes: MutableList<BakaCommandNode> = mutableListOf()

        override val parent: BakaCommandNode? = null

        override val depth: Int = 0

        override var executor: (BakaCommandNode.(CommandSender) -> Unit)? = null

        override val args: MutableMap<String, String> = mutableMapOf()

    }.apply {
        scope.invoke(this)
    }
}

internal fun BakaCommandNode.literal(command: String, scope: BakaCommandNode.() -> Unit): BakaCommandNode {
    return object : BakaCommandNode {

        override val command: String = command

        override val root: String = this@literal.root

        override val type: BakaCommandNode.Type = BakaCommandNode.Type.LITERAL

        override var permission: (CommandSender) -> Boolean = { it.hasPermission(command) }

        override val subNodes: MutableList<BakaCommandNode> = mutableListOf()

        override val parent: BakaCommandNode = this@literal

        override val depth: Int = this@literal.depth + 1

        override var executor: (BakaCommandNode.(CommandSender) -> Unit)? = null

        override val args: MutableMap<String, String> = this@literal.args

    }.apply {
        parent.subNodes.let {
            if (it.isNotEmpty()) {
                val first = it.first()
                when (first.type) {
                    BakaCommandNode.Type.LITERAL  -> {
                        if (this.type == BakaCommandNode.Type.LITERAL) {
                            it.add(this)
                        } else {
                            throw IllegalArgumentException("(${parent.command})子指令只能同时存在一种类型.已存在指令{${first.command}:[${first.type}]}类型,无法添加当前指令{${this.command}:[${this.type}]}")
                        }
                    }

                    BakaCommandNode.Type.ARGUMENT -> throw IllegalArgumentException("(${parent.command})子指令为[ARGUMENT]类型时只能有一个子指令。")
                }
            } else {
                it.add(this)
            }
        }
        scope(this)
    }
}

internal fun BakaCommandNode.argument(name: String, scope: BakaCommandNode.ArgumentCommand.() -> Unit = {}): BakaCommandNode.ArgumentCommand {
    return object : BakaCommandNode.ArgumentCommand {

        override val command: String = name

        override val root: String = this@argument.root

        override val type: BakaCommandNode.Type = BakaCommandNode.Type.ARGUMENT

        override var permission: (CommandSender) -> Boolean = { it.hasPermission(command) }

        override val tree: MutableList<BakaCommandNode> = mutableListOf()

        override val parent: BakaCommandNode = this@argument

        override val depth: Int = this@argument.depth + 1

        override var executor: (BakaCommandNode.(CommandSender) -> Unit)? = null

        override var nextSuggestions: ((CommandSender) -> List<String>?)? = null

        override var suggestions: ((CommandSender) -> List<String>?)? = null

        override val args: MutableMap<String, String> = this@argument.args
    }.apply {
        parent.subNodes.let {
            if (it.isNotEmpty()) {
                throw IllegalArgumentException("(${parent.command})子指令为[ARGUMENT]类型时只能有一个子指令。")
            } else {
                it.add(this)
            }
        }
        scope(this)
    }
}

internal fun BakaCommandNode.execute(executor: (BakaCommandContext) -> Unit): BakaCommandNode {
    this.executor = executor
    return this
}

/**
 * 应该只在[BakaCommandNode.Type.ARGUMENT]类型的节点中使用，普通节点会自动注入补全列表
 * @receiver BakaCommand
 * @param suggestions ((CommandSender) -> List<String>)?
 * @return BakaCommand
 */
internal fun BakaCommandArgumentSubNode.suggestion(suggestions: ((BakaCommandContext) -> List<String>?)?): BakaCommandNode {
    this.suggestions = suggestions
    return this
}


fun item(item: ItemStack): MutableComponent {
    return item.getDisplayNameWithCount()
}


fun item(item: String, count: Int = 0): MutableComponent {
    return wrapInSquareBrackets(literalText(item + if (count == 0) "" else "x $count")).withStyle {
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