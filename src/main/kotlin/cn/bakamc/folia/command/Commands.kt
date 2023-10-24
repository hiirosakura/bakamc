package cn.bakamc.folia.command

import cn.bakamc.folia.util.getDisplayNameWithCount
import cn.bakamc.folia.util.literal
import cn.bakamc.folia.util.wrapInSquareBrackets
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

internal fun JavaPlugin.registerCommand() {
    registerCommand(FlyCommand)
    registerCommand(SpecialItemCommand)


}

private fun JavaPlugin.registerCommand(command: BakaCommand) {
    getCommand(command.command)?.apply {
        setExecutor(command)
        tabCompleter = command
    }
}


internal interface BakaCommand : CommandExecutor, TabCompleter {

    val subCommand: String

    val command: String get() = "baka-$subCommand"

    fun item(item: ItemStack): MutableComponent {
        return item.getDisplayNameWithCount()
    }

    fun item(item: String): MutableComponent {
        return wrapInSquareBrackets(literal(item)).withStyle {
            it.applyFormat(ChatFormatting.GOLD)
        }
    }

    fun player(player: ServerPlayer): MutableComponent {
        return wrapInSquareBrackets(literal(player.displayName)).withStyle {
            it.applyFormat(ChatFormatting.AQUA)
        }
    }

    fun error(message: String = ""): MutableComponent {
        return literal(message).withStyle {
            it.applyFormat(ChatFormatting.RED)
        }
    }

    fun success(message: String = ""): MutableComponent {
        return literal(message).withStyle {
            it.applyFormat(ChatFormatting.GREEN)
        }
    }

    operator fun MutableComponent.plus(component: MutableComponent): MutableComponent {
        return this.append(component)
    }

    operator fun MutableComponent.plus(component: String): MutableComponent {
        return this.append(component)
    }


    fun ServerPlayer.feedback(vararg message: MutableComponent) {
        when {
            message.size == 1 -> this.sendSystemMessage(message[0])
            message.isEmpty() -> return
            message.size >= 2 -> {
                val msg = literal()
                message.forEach {
                    msg.append(it)
                }
                this.sendSystemMessage(msg)
            }
        }
    }

}