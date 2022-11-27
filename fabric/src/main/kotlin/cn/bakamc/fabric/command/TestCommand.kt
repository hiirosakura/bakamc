package cn.bakamc.fabric.command

import cn.bakamc.common.player.PlayerInfo
import cn.bakamc.fabric.player.FabricPlayerManager
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.command

 * 文件名 TestCommand

 * 创建时间 2022/11/26 12:50

 * @author forpleuvoir

 */
object TestCommand {
	fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
		dispatcher.register(
			literal("baka:test")
				.then(
					literal("players").executes {
						it.source.sendFeedback(Text.of("当前在线玩家数:${it.source.server.playerManager.currentPlayerCount}"), false)
						it.source.server.playerManager.playerList.forEach { p ->
							it.source.sendFeedback(p.name, false)
						}
						1
					}
				)
				.then(
					literal("baka_players").executes {
						var players: List<PlayerInfo> = emptyList()
						FabricPlayerManager.hasManager { players = it.players() }
						it.source.sendFeedback(Text.of("当前在线玩家数:${players.size}"), false)
						players.forEach { p ->
							it.source.sendFeedback(Text.of(p.name), false)
						}
						1
					}
				)
		)
	}


}