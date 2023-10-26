package cn.bakamc.folia.command

import cn.bakamc.folia.util.getDisplayNameWithCount
import cn.bakamc.folia.util.wrapInSquareBrackets
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import cn.bakamc.folia.util.literal as literalText

internal interface BakaCommand : CommandExecutor, TabCompleter {

    enum class Type {
        LITERAL,
        ARGUMENT
    }

    val command: String

    /**
     * 根指令
     */
    val root: String

    val type: Type

    val isArgument: Boolean
        get() = type == Type.ARGUMENT

    /**
     * 子命令类型只能为一种
     * 如果是[Type.ARGUMENT]类型，则[tree]只能包含一个子指令
     */
    val tree: MutableList<BakaCommand>

    var permission: (CommandSender) -> Boolean

    val parent: BakaCommand?

    val depth: Int

    var executor: (BakaCommand.(CommandSender) -> Unit)?

    /**
     * 下一个节点的补全列表
     */
    val nextSuggestions: ((CommandSender) -> List<String>?)?
        get() = { sender ->
            var result: List<String>? = null
            if (tree.isNotEmpty()) {
                val first = tree.first()
                result = if (first.isArgument) {
                    first.suggestions?.invoke(sender)
                } else {
                    tree.map { it.command }
                }
            }
            result
        }

    val suggestions: ((CommandSender) -> List<String>?)?
        get() = null

    val args: MutableMap<String, String>

    interface ArgumentCommand : BakaCommand {
        override var suggestions: ((CommandSender) -> List<String>?)?
    }

    //fly recharge ⑨币 1
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //检查发送者权限
        if (!permission(sender)) {
            sender.sendMessage("§c你没有权限执行该指令")
            return true
        }
        //当前实际执行的指令
        val commandLine: String = buildString {
            append("/${command.name}")
            args.forEach { append(" $it") }
        }
        //如果args为空，说明是根指令直接执行
        if (args.isEmpty()) {
            if (depth == 0) executor?.let { it(sender) } ?: sender.sendMessage("§c无法执行该指令$commandLine")
            return true
        }
        //如果args数量等于当前指令深度,说明指令结束立即执行
        if (args.size == depth) {
            executor?.let { it(sender) } ?: sender.sendMessage("§c无法执行该指令$commandLine")
            return true
        }

        //查找下一个节点
        for (sub in tree) {
            if (!sub.isArgument) {
                //不是参数类型，直接判断指令是否相同
                if (sub.command == args[depth]) {
                    sub.onCommand(sender, command, label, args)
                    break
                }
            } else {
                //是参数类型，注入参数
                sub.args[sub.command] = args[depth]
                sub.onCommand(sender, command, label, args)
                break
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): List<String>? {
        //检查发送者权限
        if (!permission(sender)) {
            return mutableListOf("§c你没有权限执行该指令")
        }
        args.forEach {
            println(it)
        }
        //非根指令才有补全
        if (args.isNotEmpty()) {
            if (args.size == depth) {
                return parent?.nextSuggestions?.invoke(sender)
            }
            if (args.size + depth == 1) {
                return nextSuggestions?.invoke(sender)
            }
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

}


internal fun root(command: String, scope: BakaCommand.() -> Unit): BakaCommand {
    return object : BakaCommand {

        override val command: String = command

        override val root: String = command

        override val type: BakaCommand.Type = BakaCommand.Type.LITERAL

        override var permission: (CommandSender) -> Boolean = { it.hasPermission(command) }

        override val tree: MutableList<BakaCommand> = mutableListOf()

        override val parent: BakaCommand? = null

        override val depth: Int = 0

        override var executor: (BakaCommand.(CommandSender) -> Unit)? = null

        override val args: MutableMap<String, String> = mutableMapOf()

    }.apply {
        scope.invoke(this)
    }
}

internal fun BakaCommand.literal(command: String, scope: BakaCommand.() -> Unit): BakaCommand {
    return object : BakaCommand {

        override val command: String = command

        override val root: String = this@literal.root

        override val type: BakaCommand.Type = BakaCommand.Type.LITERAL

        override var permission: (CommandSender) -> Boolean = { it.hasPermission(command) }

        override val tree: MutableList<BakaCommand> = mutableListOf()

        override val parent: BakaCommand = this@literal

        override val depth: Int = this@literal.depth + 1

        override var executor: (BakaCommand.(CommandSender) -> Unit)? = null

        override val args: MutableMap<String, String> = this@literal.args

    }.apply {
        parent.tree.let {
            if (it.isNotEmpty()) {
                val first = it.first()
                when (first.type) {
                    BakaCommand.Type.LITERAL  -> {
                        if (this.type == BakaCommand.Type.LITERAL) {
                            it.add(this)
                        } else {
                            throw IllegalArgumentException("(${parent.command})子指令只能同时存在一种类型.已存在指令{${first.command}:[${first.type}]}类型,无法添加当前指令{${this.command}:[${this.type}]}")
                        }
                    }

                    BakaCommand.Type.ARGUMENT -> throw IllegalArgumentException("(${parent.command})子指令为[ARGUMENT]类型时只能有一个子指令。")
                }
            } else {
                it.add(this)
            }
        }
        scope(this)
    }
}

internal fun BakaCommand.argument(name: String, scope: BakaCommand.ArgumentCommand.() -> Unit = {}): BakaCommand.ArgumentCommand {
    return object : BakaCommand.ArgumentCommand {

        override val command: String = name

        override val root: String = this@argument.root

        override val type: BakaCommand.Type = BakaCommand.Type.ARGUMENT

        override var permission: (CommandSender) -> Boolean = { it.hasPermission(command) }

        override val tree: MutableList<BakaCommand> = mutableListOf()

        override val parent: BakaCommand = this@argument

        override val depth: Int = this@argument.depth + 1

        override var executor: (BakaCommand.(CommandSender) -> Unit)? = null

        override var nextSuggestions: ((CommandSender) -> List<String>?)? = null

        override var suggestions: ((CommandSender) -> List<String>?)? = null

        override val args: MutableMap<String, String> = this@argument.args
    }.apply {
        parent.tree.let {
            if (it.isNotEmpty()) {
                throw IllegalArgumentException("(${parent.command})子指令为[ARGUMENT]类型时只能有一个子指令。")
            } else {
                it.add(this)
            }
        }
        scope(this)
    }
}

internal fun BakaCommand.execute(executor: BakaCommand.(CommandSender) -> Unit): BakaCommand {
    this.executor = executor
    return this
}

/**
 * 应该只在[BakaCommand.Type.ARGUMENT]类型的节点中使用，普通节点会自动注入补全列表
 * @receiver BakaCommand
 * @param suggestions ((CommandSender) -> List<String>)?
 * @return BakaCommand
 */
internal fun BakaCommand.ArgumentCommand.suggestion(suggestions: ((CommandSender) -> List<String>?)?): BakaCommand {
    this.suggestions = suggestions
    return this
}