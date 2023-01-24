package cn.bakamc.fabric.command

import cn.bakamc.fabric.chat.FabricMessageHandler
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.command

 * 文件名 ChatCommand

 * 创建时间 2022/11/22 21:39

 * @author forpleuvoir

 */
object ChatCommand {

	fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
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
					literal("reconnect").requires { it.hasPermissionLevel(4) }.executes { it ->
						FabricMessageHandler.hasHandler { it.reconnect() }
						it.source.sendFeedback(Text.of("§b[BakaMC]§a已重新连接至riguru服务器"), false)
						1
					}
				)
		)
	}

	private fun whisper(context: CommandContext<ServerCommandSource>): Int {
		val sender = context.source.player!!
		val target = StringArgumentType.getString(context, "target")
		val message = StringArgumentType.getString(context, "message")
		if (sender.name.string == target && sender.displayName.string == target) {
			context.source.sendFeedback(Text.of("§b[BakaMC]§a不要自言自语"), false)
			return 1
		}
		FabricMessageHandler.hasHandler { handler ->
			handler.sendWhisperMessage(sender, message, target)
		}
		return 1
	}


}



