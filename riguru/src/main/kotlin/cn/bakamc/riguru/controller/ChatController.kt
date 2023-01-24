package cn.bakamc.riguru.controller

import cn.bakamc.common.chat.message.PostMessage
import cn.bakamc.riguru.websocket.ChatServer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*


@Controller
@RequestMapping("/chat/{server_id}")
class ChatController {

	private val log = LoggerFactory.getLogger(ChatController::class.java)

	@PostMapping
	@ResponseBody
	fun postMessage(@RequestBody postMessage: String, @PathVariable("server_id") serverID: String) {
		val message = PostMessage.fromJson(postMessage)
		ChatServer.broadcastMessage(message, serverID)
	}

}