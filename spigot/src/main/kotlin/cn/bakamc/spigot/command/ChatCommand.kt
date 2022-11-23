package cn.bakamc.spigot.command

import cn.bakamc.spigot.chat.SpigotMessageHandler
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandListenerWrapper
import org.bukkit.entity.Player

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.command

 * 文件名 ChatCommand

 * 创建时间 2022/11/22 21:39

 * @author forpleuvoir

 */
object ChatCommand {

	fun register(dispatcher: CommandDispatcher<CommandListenerWrapper>) {
		dispatcher.register(
			literal("bakamc:chat")
				.then(
					literal("whisper").requires { true }
						.then(
							argument("target", StringArgumentType.string())
								.then(
									argument("message", StringArgumentType.string())
										.executes(this::whisper)
								)
						)
				)
				.then(
					literal("reconnect").requires { it.bukkitSender.isOp }.executes { it ->
						SpigotMessageHandler.hasHandler { it.reconnect() }
						it.source.bukkitSender.sendMessage("§b[BakaMC]§a已重新连接至riguru服务器")
						1
					}
				)
		)
	}

	private fun whisper(context: CommandContext<CommandListenerWrapper>): Int {
		val sender = context.source.bukkitSender
		val target = StringArgumentType.getString(context, "target")
		val message = StringArgumentType.getString(context, "message")
		if (sender is Player) {
			if (sender.name == target && sender.displayName == target) {
				context.source.bukkitSender.sendMessage("§b[BakaMC]§a不要自言自语")
				return 1
			}
			SpigotMessageHandler.hasHandler { handler ->
				handler.sendWhisperMessage(sender, message, target)
			}
		}
		return 1
	}


}



