package cn.bakamc.folia.command

import cn.bakamc.folia.util.getDisplayNameWithCount
import cn.bakamc.folia.util.literal as literalText
import cn.bakamc.folia.util.wrapInSquareBrackets
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

internal interface BakaCommand : CommandExecutor, TabCompleter {

    val command: String

    /**
     * 根指令
     */
    val root: String

    val isArg: Boolean

    var permission: (CommandSender) -> Boolean

    val tree: MutableList<BakaCommand>

    val parent: BakaCommand?

    val depth: Int

    var executor: (BakaCommand.(CommandSender) -> Unit)?

    var suggestions: ((CommandSender) -> MutableList<String>)?

    val args: MutableMap<String, String>

    val defaultSuggestions: MutableList<String>?
        get() = parent?.tree?.map { it.command }?.toMutableList()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        //检查发送者权限
        if (permission(sender)) {
            sender.sendMessage("§c你没有权限执行该指令")
            return true
        }
        //当前实际执行的指令
        val commandLine: String = buildString {
            append("/${command.name}")
            args?.forEach { append(" $it") }
        }
        //如果args为空，说明是根指令直接执行
        if (args.isNullOrEmpty()) {
            if (depth == 0) executor?.let { it(sender) } ?: sender.sendMessage("§c无法执行该指令$commandLine")
            return true
        }
        //如果是参数指令，则注入参数
        if (this.isArg) {
            val arg = args[depth]
            this.args[this.command] = arg
        }
        //如果args数量等于当前指令深度,说明指令结束立即执行
        if (args.size == depth) {
            executor?.let { it(sender) } ?: sender.sendMessage("§c无法执行该指令$commandLine")
        }
        //递归执行子指令
        tree.find { it.command == args[depth + 1] }?.onCommand(sender, command, label, args) ?: {
            sender.sendMessage("§c错误的指令格式$commandLine")
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>?): MutableList<String>? {
        //检查发送者权限
        if (permission(sender)) {
            return mutableListOf("§c你没有权限执行该指令")
        }
        //非根指令才有补全
        if (depth > 0 && !args.isNullOrEmpty()) {
            if (args.size == depth) {
                return suggestions?.invoke(sender)
            }
            tree.find { it.command == args[depth] }?.onTabComplete(sender, command, label, args)
        }
        return null
    }

    fun <T> getArg(key: String, map: (String) -> T): T? {
        return args[key]?.let(map)
    }

    fun getArg(key: String): String? {
        return args[key]
    }

    fun item(item: ItemStack): MutableComponent {
        return item.getDisplayNameWithCount()
    }


    fun item(item: String): MutableComponent {
        return wrapInSquareBrackets(literalText(item)).withStyle {
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

}


internal fun root(command: String, scope: BakaCommand.() -> Unit): BakaCommand {
    return object : BakaCommand {

        override val command: String = command

        override val root: String = command

        override val isArg: Boolean = false

        override var permission: (CommandSender) -> Boolean = { it.hasPermission(command) }

        override val tree: MutableList<BakaCommand> = mutableListOf()

        override val parent: BakaCommand? = null

        override val depth: Int = 0

        override var executor: (BakaCommand.(CommandSender) -> Unit)? = null

        override var suggestions: ((CommandSender) -> MutableList<String>)? = null

        override val args: MutableMap<String, String> = mutableMapOf()

    }.apply {
        scope.invoke(this)
    }
}

internal fun BakaCommand.literal(command: String, scope: BakaCommand.() -> Unit): BakaCommand {
    return object : BakaCommand {

        override val command: String = command

        override val root: String = this@literal.root

        override val isArg: Boolean = false

        override var permission: (CommandSender) -> Boolean = { it.hasPermission(command) }

        override val tree: MutableList<BakaCommand> = mutableListOf()

        override val parent: BakaCommand = this@literal

        override val depth: Int = this@literal.depth + 1

        override var executor: (BakaCommand.(CommandSender) -> Unit)? = null

        override var suggestions: ((CommandSender) -> MutableList<String>)? = defaultSuggestions?.let { suggestions -> { suggestions } }

        override val args: MutableMap<String, String> = this@literal.args

    }.apply {
        this@literal.tree.add(this)
        scope(this)
    }
}

internal fun BakaCommand.argument(name: String, scope: BakaCommand.() -> Unit = {}): BakaCommand {
    return object : BakaCommand {

        override val command: String = name

        override val root: String = this@argument.root

        override val isArg: Boolean = true

        override var permission: (CommandSender) -> Boolean = { it.hasPermission(command) }

        override val tree: MutableList<BakaCommand> = mutableListOf()

        override val parent: BakaCommand = this@argument

        override val depth: Int = this@argument.depth + 1

        override var executor: (BakaCommand.(CommandSender) -> Unit)? = null

        override var suggestions: ((CommandSender) -> MutableList<String>)? = null

        override val args: MutableMap<String, String> = this@argument.args
    }.apply {
        scope(this)
    }
}

internal fun BakaCommand.execute(executor: BakaCommand.(CommandSender) -> Unit): BakaCommand {
    this.executor = executor
    return this
}

internal fun BakaCommand.suggestions(suggestions: ((CommandSender) -> MutableList<String>)?): BakaCommand {
    this.suggestions = suggestions
    return this
}