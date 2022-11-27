package cn.bakamc.common.player

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType.Player.PLAYER_JOIN
import cn.bakamc.common.api.WSMessageType.Player.PLAYER_LEFT
import cn.bakamc.common.api.WSMessageType.Player.PLAYER_SYNC_ALL_DATA
import cn.bakamc.common.common.MultiPlatform
import cn.bakamc.common.common.SimpleWebSocketClient
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.utils.parseToJsonArray
import cn.bakamc.common.utils.parseToJsonObject
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
abstract class PlayerManager<T, P, S>(val config: ServerConfig, val server: S) : MultiPlatform<T, P, S> {

	/**
	 * 所有服务器在线的玩家
	 */
	private val players: Deque<PlayerInfo> = ConcurrentLinkedDeque()

	/**
	 * 当前服务器在线的玩家
	 */
	private val currentPlayers: Deque<P> = ConcurrentLinkedDeque()

	fun players(): List<PlayerInfo> {
		return players.toList()
	}

	fun onlinePlayers(): List<P> {
		return currentPlayers.toList()
	}

	fun syncData() {
		webSocketClient.send(WSMessage(PLAYER_SYNC_ALL_DATA))
		currentPlayers.clear()
		server.players().forEach {
			currentPlayers.add(it)
		}
	}

	fun onPlayerJoin(player: P) {
		webSocketClient.send(WSMessage(PLAYER_JOIN, player.playerInfo()))
		players.add(player.playerInfo())
		currentPlayers.add(player)
	}

	fun onPlayerLeft(player: P) {
		webSocketClient.send(WSMessage(PLAYER_LEFT, player.playerInfo()))
		players.remove(player.playerInfo())
		currentPlayers.remove(player)
	}

	protected open val webSocketClient =
		SimpleWebSocketClient("player", "${config.riguruWebSocketAddress}/player/${config.serverId}")
			.onMessage(::onMessage)

	fun connect() = webSocketClient.connect()

	fun reconnect() = webSocketClient.reconnect()

	fun close() = webSocketClient.close()

	protected open fun onMessage(message: WSMessage) {
		when (message.type) {
			PLAYER_SYNC_ALL_DATA -> {
				players.clear()
				players.addAll(message.data.parseToJsonArray.map { PlayerInfo.deserialize(it) })
			}

			PLAYER_JOIN          -> {
				players.add(PlayerInfo.deserialize(message.data.parseToJsonObject))
			}

			PLAYER_LEFT          -> {
				players.remove(PlayerInfo.deserialize(message.data.parseToJsonObject))
			}
		}
	}

}