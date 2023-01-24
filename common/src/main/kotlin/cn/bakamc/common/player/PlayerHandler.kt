package cn.bakamc.common.player

import cn.bakamc.common.api.WebSocketHandler
import cn.bakamc.common.common.MultiPlatform
import cn.bakamc.common.common.SimpleWebSocketClient
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.utils.net.httpGet
import cn.bakamc.common.utils.net.httpPost
import cn.bakamc.common.utils.parseToJsonArray
import cn.bakamc.common.utils.toJsonStr
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * 玩家管理器 应该是保存所有服务器在线的玩家信息

 * 项目名 bakamc

 * 包名 cn.bakamc.common.player

 * 文件名 PlayerManager

 * 创建时间 2022/9/6 23:12

 * @author forpleuvoir

 */
abstract class PlayerHandler<T, P, S>(val config: ServerConfig, val server: S) : MultiPlatform<T, P, S>, WebSocketHandler {

	/**
	 * 所有服务器在线的玩家
	 */
	private val players: Deque<PlayerInfo> = ConcurrentLinkedDeque()

	/**
	 * 当前服务器在线的玩家
	 */
	private val currentPlayers: Deque<P> = ConcurrentLinkedDeque()

	override val webSocketClient =
		SimpleWebSocketClient("player", "${config.riguruWebSocketAddress}/player/${config.serverId}")
			.onMessage(::onMessage)
			.salt(config.riguruSecret)

	private fun url(path: String): String {
		return "${config.riguruHttpAddress}/player/${config.serverId}/${path}"
	}

	fun players(): List<PlayerInfo> {
		return players.toList()
	}

	fun onlinePlayers(): List<P> {
		return currentPlayers.toList()
	}

	fun syncData() {
		httpGet(url("sync")).sendAsync {}
		currentPlayers.clear()
		server.players().forEach {
			currentPlayers.add(it)
		}
	}

	fun onPlayerJoin(player: P) {
		httpPost(url("join"), player.playerInfo().toJsonStr()).sendAsync {}
		players.add(player.playerInfo())
		currentPlayers.add(player)
	}

	fun onPlayerLeft(player: P) {
		httpPost(url("left"), player.playerInfo().toJsonStr()).sendAsync {}
		players.remove(player.playerInfo())
		currentPlayers.remove(player)
	}

	protected open fun onMessage(message: String) {
		try {
			val array = message.parseToJsonArray
			players.clear()
			array.forEach {
				val playerInfo = PlayerInfo.deserialize(it)
				players.add(playerInfo)
			}
		} catch (e: Exception) {
			println("玩家信息解析失败:${message}")
		}
	}

}