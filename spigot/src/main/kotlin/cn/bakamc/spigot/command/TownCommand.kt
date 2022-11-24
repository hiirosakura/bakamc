package cn.bakamc.spigot.command

import cn.bakamc.common.town.TownApplication
import cn.bakamc.common.utils.isIn
import cn.bakamc.spigot.common.SpigotPlatform
import cn.bakamc.spigot.town.SpigotTownManager
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ClickEvent.Action
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.commands.CommandListenerWrapper
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
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
//				.then(
//					literal("application").requires { true }
//						.then(
//							argument("town", StringArgumentType.string()).suggests(this::townSuggests)
//								.then(
//									argument("message", StringArgumentType.string())
//										.executes(this::application)
//								)
//						)
//				)
//				.then(
//					literal("approveApplication").requires { true }
//						.then(
//							argument("town", StringArgumentType.string())
//								.then(
//									argument("applicant_uuid", StringArgumentType.string())
//										.executes(this::approveApplication)
//								)
//						)
//				)
				.then(
					literal("syncData").requires { it.bukkitSender.isOp }
						.executes {
							SpigotTownManager.hasManager{it.syncData()}
							1
						}
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
		//todo 未完成
		val sender = context.source.bukkitSender
		if (sender is Player) {
			val townName = StringArgumentType.getString(context, "town")
			val message = StringArgumentType.getString(context, "message")
			SpigotTownManager.hasManager { townManager ->
				val town = townManager[townName]!!
				val s = SpigotPlatform.playerInfo(sender)
				val application = TownApplication(0, town.id, s, message, Date())
				townManager.application(application) { success ->
					if (success) {
						sender.sendMessage("º[${town.name}]请求发送成功")
						Bukkit.getServer().onlinePlayers.forEach {
							if (SpigotPlatform.playerInfo(it).isIn(town.admin)) {
								it.spigot()
									.sendMessage(TextComponent("[${town.name}]有人(${s.name})申请加入小镇"))
								it.spigot().sendMessage(TextComponent("§2[√]").apply {
									clickEvent = ClickEvent(Action.RUN_COMMAND, "/bakamc:approveApplication ${town.name} \"${s.uuid()}\"")
									addExtra(TextComponent(" §4[×]"))
								})
							}
						}
					} else {
						sender.sendMessage("[${town.name}]请求发送失败")
					}
				}
			}
			return 1
		}
		return 0
	}

	private fun approveApplication(context: CommandContext<CommandListenerWrapper>): Int {
		val sender = context.source.bukkitSender
		if (sender is Player) {
			val townName = StringArgumentType.getString(context, "town")
			val uuid = StringArgumentType.getString(context, "applicant_uuid")
			SpigotTownManager.hasManager { townManager ->
				townManager.approveApplication(townManager[townName]!!, SpigotPlatform.playerInfo(sender), sender.isOp, UUID.fromString(uuid)) {
					if (it) {
						sender.sendMessage("º[${townManager[townName]!!.name}]批准申请成功")
					} else {
						sender.sendMessage("[${townManager[townName]!!.name}]批准申请失败")
					}
				}
			}
			return 1
		}
		return 0
	}

}