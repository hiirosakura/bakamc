package cn.bakamc.riguru.town

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType.Town.TOWN_SYNC_ALL_DATA
import cn.bakamc.common.api.parseToWSMessage
import cn.bakamc.common.utils.jsonArray
import cn.bakamc.riguru.chat.ChatServer
import cn.bakamc.riguru.services.TownServices
import cn.bakamc.riguru.util.sendMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.websocket.OnClose
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.ServerEndpoint

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.town

 * 文件名 TownServer

 * 创建时间 2022/9/11 14:39

 * @author forpleuvoir

 */
@ServerEndpoint("/town")
@Component
class TownServer(@Autowired val townServices: TownServices) {

	companion object {

		private val log = LoggerFactory.getLogger(ChatServer::class.java)

		private val sessions: MutableList<Session> = ArrayList()

	}

	@OnOpen
	fun onOpen(session: Session) {
		sessions.add(session)
		log.info("有人订阅了小镇系统服务！")
	}

	@OnClose
	fun onClose(session: Session) {
		if (sessions.remove(session)) {
			log.info("有人退订了小镇系统服务!")
		}
	}

	@OnMessage
	fun onMessage(message: String, session: Session) {
		message.parseToWSMessage(
			wsMessage = {
				when (type) {
					TOWN_SYNC_ALL_DATA -> syncData(session)
					else               -> log.error("[${message}]无法解析的消息格式")
				}
			},
			onException = {
				log.error("[${message}]无法解析的消息格式", it)
			}
		)
	}

	fun syncData(session: Session) {
		val towns = townServices.getAll().values
		session.sendMessage(WSMessage(TOWN_SYNC_ALL_DATA, jsonArray(towns).toString()))
	}


}