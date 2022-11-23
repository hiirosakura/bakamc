package cn.bakamc.common.common

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

/**
 * websocket客户端

 * 项目名 bakamc

 * 包名 cn.bakamc.common.common

 * 文件名 SimpleWebSocketClient

 * 创建时间 2022/8/28 17:23

 * @author forpleuvoir

 */
class SimpleWebSocketClient(uri: String, var onMessage: (String) -> Unit) : WebSocketClient(URI.create(uri)) {

	var onOpen: ((ServerHandshake) -> Unit)? = null

	var onClose: ((code: Int, reason: String, remote: Boolean) -> Unit)? = null

	var onError: ((Exception) -> Unit)? = {
		it.printStackTrace()
	}

	override fun onOpen(handshakedata: ServerHandshake) {
		println("[BakaMC]服务已连接")
		onOpen?.invoke(handshakedata)
	}

	override fun onMessage(message: String) {
		try {
			onMessage.invoke(message)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	override fun onClose(code: Int, reason: String, remote: Boolean) {
		println("[BakaMC]服务断开{code:$code,reason:$reason,remote:$remote}")
		onClose?.invoke(code, reason, remote)
	}

	override fun onError(ex: Exception) {
		println("[BakaMC]服务出错")
		onError?.invoke(ex)
	}
}