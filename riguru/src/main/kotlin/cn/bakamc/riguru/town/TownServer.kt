package cn.bakamc.riguru.town

import cn.bakamc.common.town.TownManager
import cn.bakamc.riguru.chat.ChatServer
import org.slf4j.LoggerFactory
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
class TownServer {

	companion object {

		private val log = LoggerFactory.getLogger(ChatServer::class.java)

		private val manager = object : TownManager() {
			override fun syncData(onSuccess: () -> Unit, onException: (Exception) -> Unit) {

			}
		}

		private val connects: MutableList<Session> = ArrayList()

	}

	@OnMessage
	fun onMessage(){

	}

	@OnOpen
	fun onOpen(session: Session) {
		connects.add(session)
		log.info("有人订阅了小镇系统服务！")
	}

	@OnClose
	fun onClose(session: Session) {
		if (connects.remove(session)) {
			log.info("有人退订了小镇系统服务!")
		}
	}



}