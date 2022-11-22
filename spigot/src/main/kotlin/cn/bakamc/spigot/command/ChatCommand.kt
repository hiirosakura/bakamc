package cn.bakamc.spigot.command

import cn.bakamc.spigot.chat.SpigotMessageHandler
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import net.minecraft.commands.CommandListenerWrapper
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.command

 * 文件名 ChatCommand

 * 创建时间 2022/11/22 21:39

 * @author forpleuvoir

 */
object ChatCommand {

	fun registerCommand(plugin: JavaPlugin) {
		plugin.server.apply {
			getPluginCommand("whisper")?.setExecutor(::whisper)
		}
	}

	fun register(dispatcher: CommandDispatcher<CommandListenerWrapper>) {
		dispatcher.register(
			literal<CommandListenerWrapper>("bakamc:whisper")
				.then(
					argument<CommandListenerWrapper?, String?>("target", StringArgumentType.string())
						.then(
							argument<CommandListenerWrapper?, String?>("message", StringArgumentType.string())
								.executes {
									val sender = it.source.bukkitSender
									val target = StringArgumentType.getString(it, "target")
									val message = StringArgumentType.getString(it, "message")
									if (sender is Player) {
										if (sender.name == target && sender.displayName == target)
											return@executes 1
										SpigotMessageHandler.hasHandler { handler ->
											handler.sendWhisperMessage(sender, message, target)
										}
									}
									1
								}
						)
				)
		)
	}

	private fun whisper(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
		if (args.size < 2) return false
		if (sender is Player && sender.isOnline) {
			if (sender.name == args[0] || sender.displayName == args[0]) {
				sender.sendMessage("§c不要自言自语")
				return true
			}
			val message = StringBuilder()
			args.forEachIndexed { index, s ->
				if (index != 0) {
					message.append(s)
				}
				if (index != args.size - 1) {
					message.append(" ")
				}
			}
			SpigotMessageHandler.hasHandler {
				it.sendWhisperMessage(sender, message.toString(), args[0])
			}
		}
		return false
	}


}



