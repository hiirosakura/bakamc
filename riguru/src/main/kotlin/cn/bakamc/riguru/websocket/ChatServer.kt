package cn.bakamc.riguru.websocket

import cn.bakamc.common.chat.message.MessageType.Chat
import cn.bakamc.common.chat.message.MessageType.Whisper
import cn.bakamc.common.chat.message.PostMessage
import cn.bakamc.common.utils.toJsonStr
import cn.bakamc.riguru.util.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.websocket.OnClose
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

/**
 * 聊天系统服务器

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.websocket

 * 文件名 ChatServer

 * 创建时间 2022/8/30 21:16

 * @author forpleuvoir

 */
@Suppress("unused")
@ServerEndpoint("/chat/{server_id}")
@Component
class ChatServer {

	companion object {

		private val log = LoggerFactory.getLogger(ChatServer::class.java)

		private val sessions: MutableList<Session> = ArrayList()

		fun broadcastMessage(message: PostMessage, serverID: String) {
			sessions.broadcast(message.toJsonStr())
			when (message.type) {
				Chat    -> log.info("[({})chat]{}", serverID, message.finalMessage)
				Whisper -> log.info("[({})whisper]{}", serverID, message.finalMessage)
			}
		}

	}

	@OnOpen
	fun onOpen(
		session: Session,
		@PathParam("server_id") id: String
	) {
		sessions.add(session)
		log.info("[{}]有人订阅了昆虫通知服务！", id)
	}

	@OnClose
	fun onClose(
		session: Session,
		@PathParam("server_id") id: String
	) {
		sessions.remove(session)
		log.info("[{}]有人退订了昆虫通知服务！", id)
	}


}