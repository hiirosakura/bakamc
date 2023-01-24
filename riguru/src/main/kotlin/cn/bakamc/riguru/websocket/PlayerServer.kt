package cn.bakamc.riguru.websocket

import cn.bakamc.common.player.PlayerInfo
import cn.bakamc.common.utils.jsonArray
import cn.bakamc.common.utils.toJsonStr
import cn.bakamc.riguru.services.PlayerInfoServices
import cn.bakamc.riguru.util.broadcast
import cn.bakamc.riguru.util.sendMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import javax.websocket.OnClose
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

		lateinit var playerServices: PlayerInfoServices

		fun onPlayerJoin(playerInfo: PlayerInfo, serverID: String) {
			playerServices.saveOrUpdate(playerInfo)
			players.add(playerInfo)
			syncData()
			log.info("[{}]有玩家({})加入服务器了", serverID, playerInfo.name)
		}

		fun onPlayerLeft(playerInfo: PlayerInfo, serverID: String) {
			players.remove(playerInfo)
			syncData()
			log.info("[{}]有玩家({})离开服务器了", serverID, playerInfo.name)
		}

		fun syncData() {
			sessions.broadcast(jsonArray(players).toJsonStr())
		}

		fun syncData(session: Session) {
			session.sendMessage(jsonArray(players).toJsonStr())
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

}