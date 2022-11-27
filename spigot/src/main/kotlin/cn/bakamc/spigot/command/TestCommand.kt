package cn.bakamc.spigot.command

import cn.bakamc.common.player.PlayerInfo
import cn.bakamc.spigot.player.SpigotPlayerManager
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandListenerWrapper

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.command

 * 文件名 TestCommand

 * 创建时间 2022/11/26 12:50

 * @author forpleuvoir

 */
object TestCommand {

	fun register(dispatcher: CommandDispatcher<CommandListenerWrapper>) {
		dispatcher.register(
			literal("baka:test")
				.then(
					literal("players").executes {
						val server = it.source.bukkitSender.server
						it.source.bukkitSender.sendMessage("当前在线玩家数:${server.onlinePlayers.size}")
						server.onlinePlayers.forEach { p ->
							it.source.bukkitSender.sendMessage(p.name)
						}
						1
					}
				)
				.then(
					literal("baka_players").executes {
						var players: List<PlayerInfo> = emptyList()
						SpigotPlayerManager.hasManager { players = it.players() }
						it.source.bukkitSender.sendMessage("当前在线玩家数:${players.size}")
						players.forEach { p ->
							it.source.bukkitSender.sendMessage(p.name)
						}
						1
					}
				)
		)
	}


}