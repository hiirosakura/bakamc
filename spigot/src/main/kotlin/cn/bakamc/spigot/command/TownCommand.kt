package cn.bakamc.spigot.command

import cn.bakamc.common.town.Town
import cn.bakamc.common.town.Town.Companion
import cn.bakamc.common.town.TownApplication
import cn.bakamc.spigot.common.SpigotPlatform
import cn.bakamc.spigot.town.SpigotTownManager
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.CommandListenerWrapper
import org.bukkit.entity.Player
import java.util.Date
import java.util.concurrent.CompletableFuture

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.command

 * 文件名 TownCommand

 * 创建时间 2022/11/23 13:10

 * @author forpleuvoir

 */
object TownCommand {

	fun register(dispatcher: CommandDispatcher<CommandListenerWrapper>) {
		dispatcher.register(
			literal("bakamc:town")
				.then(
					literal("application").requires { true }
						.then(
							argument("town", StringArgumentType.string()).suggests(this::townSuggests)
								.then(
									argument("message", StringArgumentType.string())
										.executes(this::application)
								)
						)
				)
				.then(
					literal("approveApplication").requires { true }
						.then(
							argument("town", StringArgumentType.string())
								.then(
									argument("applicant_name", StringArgumentType.string())
										.executes(this::approveApplication)
								)
						)
				)
		)
	}

	private fun townSuggests(context: CommandContext<CommandListenerWrapper>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
		var list = emptyList<String>()
		SpigotTownManager.hasManager {
			list = it.townNames
		}
		return suggest(list, builder)
	}


	private fun application(context: CommandContext<CommandListenerWrapper>): Int {
		val sender = context.source.bukkitSender
		if (sender is Player) {
			val townName = StringArgumentType.getString(context, "town")
			val message = StringArgumentType.getString(context, "message")
			var town: Town = Town.NONE
			SpigotTownManager.hasManager {
				town = it[townName]!!
			}
			val application = TownApplication(0, town.id, SpigotPlatform.playerInfo(sender), message, Date())


			return 1
		}
		return 0
	}

	private fun approveApplication(context: CommandContext<CommandListenerWrapper>): Int {


		return 1
	}

}