package cn.bakamc.riguru.chat

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType.Chat.CHAT_CONFIG
import cn.bakamc.common.api.WSMessageType.Chat.CHAT_MESSAGE
import cn.bakamc.common.api.WSMessageType.Chat.REGISTRY_SERVER_INFO
import cn.bakamc.common.api.WSMessageType.Chat.WHISPER_MESSAGE
import cn.bakamc.common.api.parseToWSMessage
import cn.bakamc.common.common.ServerInfo
import cn.bakamc.common.utils.gson
import cn.bakamc.riguru.config.ServerChatConfig
import cn.bakamc.riguru.util.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import javax.websocket.OnClose
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

/**
 * 聊天系统服务器

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.chat

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
		serverInfos[id] = ServerInfo(id, id, listOf("没有描述"))
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
		message.parseToWSMessage(
			wsMessage = {
				when (type) {
					CHAT_CONFIG -> getConfig(session)
					REGISTRY_SERVER_INFO -> registryServerInfo(data, session, id)
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

	private fun getConfig(session: Session) {
		session.sendMessage(WSMessage(CHAT_CONFIG, ServerChatConfig.serialization.toString()))
	}

	private fun registryServerInfo(json: String, session: Session, id: String) {
		val serverInfo = gson.fromJson(json, ServerInfo::class.java)
		serverInfos[id] = serverInfo
		session.sendMessage(WSMessage(REGISTRY_SERVER_INFO, "注册服务器信息成功"))
		log.info("[{}]注册服务器信息!", id)
	}

	private fun chatMessage(json: String, session: Session, id: String) {
		sessions.values.broadcast(WSMessage(CHAT_MESSAGE, json))
		log.info("[({})chat]{}", id, json)
	}

	private fun whisperMessage(json: String, session: Session, id: String) {
		sessions.values.broadcast(WSMessage(WHISPER_MESSAGE, json))
		log.info("[({})chat]{}", id, json)
	}

	companion object {

		private val log = LoggerFactory.getLogger(ChatServer::class.java)

		private val sessions = ConcurrentHashMap<String, Session>()

		private val serverInfos = ConcurrentHashMap<String, ServerInfo>()
	}

}