package cn.bakamc.velocity.event

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChatEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.audience.MessageType.CHAT
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.velocity.event

 * 文件名 PlayerEvent

 * 创建时间 2022/11/27 15:56

 * @author forpleuvoir

 */
class PlayerEvent(private val server: ProxyServer) {

	@Subscribe
	fun onChat(event: PlayerChatEvent) {
		println(event.message)
		server.allPlayers.forEach {
			it.sendMessage(Identity.identity(event.player.uniqueId), Component.text(event.message), CHAT)
		}
	}

	@Subscribe
	fun onPlayerJoin(event: ServerConnectedEvent) {
		for (server in server.allServers) {
			server.sendMessage(Component.text("${server.serverInfo.name}有玩家加入了服务器:${event.player.username}"))
		}
		server.allPlayers.forEach {
			it.tabList
		}
	}

}