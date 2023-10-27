package cn.bakamc.folia.command.base

import cn.bakamc.folia.util.toServerPlayer
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BakaCommandContext
internal constructor(val sender: CommandSender, val root: String, first: BakaCommandNode) {

    var level: Int = 0
        private set

    val commandChain: MutableList<BakaCommandNode> = mutableListOf(first)

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

    val args: MutableMap<String, String> = mutableMapOf()

    val player: ServerPlayer?
        get() = (sender as? Player)?.toServerPlayer()

    fun getArg(key: String): String? {
        return args[key]
    }

    fun <T> getArg(key: String, map: (String) -> T): T? {
        return args[key]?.let(map)
    }

    fun feedback(message: Component) {
        when (sender) {
            is Player -> player!!.sendSystemMessage(message)
            else      -> sender.sendMessage(message.string)
        }
    }
}