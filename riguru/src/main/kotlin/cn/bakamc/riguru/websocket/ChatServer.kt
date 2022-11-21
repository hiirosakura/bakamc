package cn.bakamc.riguru.websocket

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType.Chat.CHAT_MESSAGE
import cn.bakamc.common.api.WSMessageType.Chat.WHISPER_MESSAGE
import cn.bakamc.common.api.parseToWSMessage
import cn.bakamc.riguru.util.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.websocket.OnClose
import javax.websocket.OnMessage
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
@ServerEndpoint("/chat/{server_id}")
@Component
class ChatServer {

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
		log.info("[{}]有人订阅了昆虫通知服务！", id)
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
					CHAT_MESSAGE -> chatMessage(data, session, id)
					WHISPER_MESSAGE -> whisperMessage(data, session, id)
					else -> log.error("[${message}]无法解析的消息格式")
				}
			},
			onException = {
				log.error("[${message}]无法解析的消息格式", it)
			}
		)
	}

	private fun chatMessage(json: String, session: Session, id: String) {
		sessions.broadcast(WSMessage(CHAT_MESSAGE, json))
		log.info("[({})chat]{}", id, json)
	}

	private fun whisperMessage(json: String, session: Session, id: String) {
		sessions.broadcast(WSMessage(WHISPER_MESSAGE, json))
		log.info("[({})chat]{}", id, json)
	}

	companion object {

		private val log = LoggerFactory.getLogger(ChatServer::class.java)

		private val sessions: MutableList<Session> = ArrayList()

	}

}