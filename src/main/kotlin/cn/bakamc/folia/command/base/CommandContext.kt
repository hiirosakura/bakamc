package cn.bakamc.folia.command.base

import cn.bakamc.folia.util.literalText
import cn.bakamc.folia.util.toServerPlayer
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandContext<S : CommandSender>
internal constructor(
    val sender: S,
    val root: String,
    val args: List<String>
) {

    var level: Int = -1
        private set

    val commandChain: MutableList<CommandNode> = mutableListOf()

    /**
     * 正在输入的节点
     */
    val building: String
        get() = args.last()

    fun next(currentNode: CommandNode): CommandContext<S> {
        level++
        commandChain.add(currentNode)
        return this
    }

    fun currentNodeString(): String {
        return if (level > 0)
            args[level - 1]
        else root
    }

    fun nextNodeString(): String? {
        return runCatching { args[level] }.getOrNull()
    }

    inline fun hasNext(action: CommandContext<S>.(String) -> Unit): CommandContext<S> {
        if (level < args.size) action(this, nextNodeString()!!)
        return this
    }

    inline fun isEnd(action: CommandContext<S>.(String) -> Unit): CommandContext<S> {
        if (level >= args.size) action(this, currentNodeString())
        return this
    }

    val commandLine: String
        get() {
            return buildString {
                append("/")
                commandChain.forEachIndexed { index, it ->
                    append(it.command)
                    if (index != commandChain.size - 1) append(" ")
                }
            }
        }

    private val arguments: MutableMap<String, String> = mutableMapOf()

    val player: ServerPlayer?
        get() = (sender as? Player)?.toServerPlayer()

    fun putArg(key: String, value: String) {
        arguments[key] = value
    }

    fun getArg(key: String): String? {
        return arguments[key]

    }

    fun <T> getArg(key: String, map: (String) -> T): T? {
        return arguments[key]?.let(map)
    }

    fun feedback(message: String) {
        feedback(literalText(message))
    }

    fun feedback(message: Component) {
        when (sender) {
            is Player -> player!!.sendSystemMessage(message)
            else      -> sender.sendMessage(JSONComponentSerializer.json().deserialize(Component.Serializer.toJson(message)))
        }
    }
}