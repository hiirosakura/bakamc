package cn.bakamc.riguru.websocket

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType.Player
import cn.bakamc.common.api.WSMessageType.Player.PLAYER_SYNC_ALL_DATA
import cn.bakamc.common.api.parseToWSMessage
import cn.bakamc.common.player.PlayerInfo
import cn.bakamc.common.utils.jsonArray
import cn.bakamc.common.utils.parseToJsonObject
import cn.bakamc.riguru.services.PlayerInfoServices
import cn.bakamc.riguru.util.broadcast
import cn.bakamc.riguru.util.sendMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import javax.websocket.OnClose
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.websocket

 * 文件名 PlayerServer

 * 创建时间 2022/11/25 17:20

 * @author forpleuvoir

 */
@ServerEndpoint("/player/{server_id}")
@Component
class PlayerServer {

	companion object {

		private val log = LoggerFactory.getLogger(PlayerServer::class.java)

		private val sessions: MutableList<Session> = ArrayList()

		private val players: Deque<PlayerInfo> = ConcurrentLinkedDeque()

		@Autowired
		lateinit var playerServices: PlayerInfoServices
		fun syncData() {
			sessions.broadcast(WSMessage(PLAYER_SYNC_ALL_DATA, jsonArray(players)))
		}

	}

	@Autowired
	fun setPlayerServices(playerServices: PlayerInfoServices) {
		PlayerServer.playerServices = playerServices
	}

	@OnOpen
	fun onOpen(
		session: Session,
		@PathParam("server_id") id: String
	) {
		sessions.add(session)
		syncData(session)
		log.info("[{}]有人订阅了玩家服务！", id)
	}

	@OnClose
	fun onClose(
		session: Session,
		@PathParam("server_id") id: String
	) {
		sessions.remove(session)
		log.info("[{}]有人退订了玩家服务！", id)
	}

	@OnMessage
	fun onMessage(
		message: String,
		session: Session,
		@PathParam("server_id") id: String
	) {
		message.parseToWSMessage(
			wsMessage = {
				when (type) {
					Player.PLAYER_JOIN -> onPlayerJoin(data, session, id)
					Player.PLAYER_LEFT -> onPlayerLeft(data, session, id)
					PLAYER_SYNC_ALL_DATA -> syncData(session)
					else -> log.error("[${message}]无法解析的消息格式")
				}
			},
			onException = {
				log.error("[${message}]无法解析的消息格式", it)
			}
		)
	}

	fun onPlayerJoin(data: String, session: Session, id: String) {
		val player = PlayerInfo.deserialize(data.parseToJsonObject)
		playerServices.saveOrUpdate(player)
		players.add(player)
		syncData()
		log.info("[{}]有玩家({})加入服务器了", id, player.name)
	}

	fun onPlayerLeft(data: String, session: Session, id: String) {
		val player = PlayerInfo.deserialize(data.parseToJsonObject)
		players.remove(player)
		syncData()
		log.info("[{}]有玩家({})离开服务器了", id, player.name)
	}

	fun syncData(session: Session) {
		session.sendMessage(WSMessage(PLAYER_SYNC_ALL_DATA, jsonArray(players)))
	}

}