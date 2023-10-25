package cn.bakamc.folia.command

import cn.bakamc.folia.util.getDisplayNameWithCount
import cn.bakamc.folia.util.wrapInSquareBrackets
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin
import cn.bakamc.folia.util.literal as literalText

internal fun JavaPlugin.registerCommand() {
    registerCommand(FlyCommand)
    registerCommand(SpecialItemCommand)
}

private fun JavaPlugin.registerCommand(command: BakaCommand) {
    getCommand(command.command)?.apply {
        logger.info("注册指令: /${command.command}")
        setExecutor(command)
        logger.info("注册指令补全: /${command.command}")
        tabCompleter = command
    }
}


internal interface BakaCommand : CommandExecutor, TabCompleter {

    val command: String

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

class CommandNode(
    val command: String,
) {


}