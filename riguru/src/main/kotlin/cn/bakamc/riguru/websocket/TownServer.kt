package cn.bakamc.riguru.websocket

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType.Town.TOWN_SYNC_ALL_DATA
import cn.bakamc.common.api.parseToWSMessage
import cn.bakamc.common.utils.jsonArray
import cn.bakamc.riguru.services.TownServices
import cn.bakamc.riguru.util.broadcast
import cn.bakamc.riguru.util.sendMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
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

 * 文件名 TownServer

 * 创建时间 2022/9/11 14:39

 * @author forpleuvoir

 */
@ServerEndpoint("/town/{server_id}")
@Component
class TownServer {

	@Autowired
	private lateinit var townServices: TownServices

	companion object {

		private val log = LoggerFactory.getLogger(TownServer::class.java)

		val sessions: MutableList<Session> = ArrayList()

	}

	@OnOpen
	fun onOpen(session: Session, @PathParam("server_id") id: String) {
		sessions.add(session)
		log.info("[{}]有人订阅了小镇系统服务！", id)
	}

	@OnClose
	fun onClose(session: Session, @PathParam("server_id") id: String) {
		if (sessions.remove(session)) {
			log.info("[{}]有人退订了小镇系统服务!", id)
		}
	}

	@OnMessage
	fun onMessage(message: String, session: Session, @PathParam("server_id") id: String) {
		message.parseToWSMessage(
			wsMessage = {
				when (type) {
					TOWN_SYNC_ALL_DATA -> syncData(session,id)
					else               -> log.error("[${message}]无法解析的消息格式")
				}
			},
			onException = {
				log.error("[${message}]无法解析的消息格式", it)
			}
		)
	}

	fun syncData(session: Session, id: String) {
		val towns = townServices.getAll().values
		session.sendMessage(WSMessage(TOWN_SYNC_ALL_DATA, jsonArray(towns).toString()))
		log.info("[{}]",id)
	}



}