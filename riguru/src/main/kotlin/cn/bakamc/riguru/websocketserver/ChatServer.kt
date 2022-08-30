package cn.bakamc.riguru.websocketserver

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType.CHAT_MESSAGE
import cn.bakamc.common.api.WSMessageType.CONFIG
import cn.bakamc.common.api.WSMessageType.REGISTRY_SERVER_INFO
import cn.bakamc.common.api.WSMessageType.WHISPER_MESSAGE
import cn.bakamc.common.chat.ServerInfo
import cn.bakamc.common.utils.gson
import cn.bakamc.common.utils.toJsonStr
import cn.bakamc.riguru.config.ServerChatConfig
import jakarta.websocket.OnClose
import jakarta.websocket.OnMessage
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * 聊天系统服务器

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.websocketserver

 * 文件名 ChatServer

 * 创建时间 2022/8/30 21:16

 * @author forpleuvoir

 */
@ServerEndpoint("/chat/{server_id}")
@Component
class ChatServer {

	@OnOpen
	fun onOpen(
		@PathParam("server_id") id: String,
		session: Session
	) {
		sessions[id] = session
		serverInfos[id] = ServerInfo(id, id, "没有描述")
		log.info("[{}]有人订阅了昆虫通知服务！", id)
	}

	@OnClose
	fun onClose(
		session: Session,
		@PathParam("server_id") id: String,
	) {
		sessions.remove(id)
		log.info("[{}]有人退订了昆虫通知服务！", id)
	}

	@OnMessage
	fun onMessage(
		message: String,
		@PathParam("server_id") id: String,
	) {
		val session = sessions[id]!!
		try {
			gson.fromJson(message, WSMessage::class.java).run {
				when (type) {
					CONFIG               -> getConfig(session)
					REGISTRY_SERVER_INFO -> registryServerInfo(data, session, id)
					CHAT_MESSAGE         -> chatMessage(data, session, id)
					WHISPER_MESSAGE      -> whisperMessage(data, session, id)
				}
			}
		} catch (e: Exception) {
			log.error("[${message}]无法理解的消息格式", e)
		}
	}

	private fun getConfig(session: Session) {
		session.sendMessage(WSMessage(CONFIG, ServerChatConfig.serialization.toString()))
	}

	private fun registryServerInfo(json: String, session: Session, id: String) {
		val serverInfo = gson.fromJson(json, ServerInfo::class.java)
		serverInfos[id] = serverInfo
		session.sendMessage(WSMessage(REGISTRY_SERVER_INFO,"注册服务器信息成功"))
		log.info("[{}]注册服务器信息!", id)
	}

	private fun chatMessage(json: String, session: Session, id: String) {

	}

	private fun whisperMessage(json: String, session: Session, id: String) {}

	companion object {

		private val log = LoggerFactory.getLogger(ChatServer::class.java)

		private val sessions = ConcurrentHashMap<String, Session>()

		private val serverInfos = ConcurrentHashMap<String, ServerInfo>()

		private fun broadcast(message: String) {
			sessions.values.forEach { it.sendMessage(message) }
		}

		private fun Session.sendMessage(message: String) {
			this.basicRemote.sendText(message)
		}

		private fun Session.sendMessage(message: WSMessage) {
			sendMessage(message.toJsonStr())
		}

	}

}