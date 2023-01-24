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
class SimpleWebSocketClient(val name: String, uri: String) : WebSocketClient(URI.create(uri)) {

	fun onMessage(action: (String) -> Unit): SimpleWebSocketClient {
		onMessage = action
		return this
	}

	/**
	 * 加密盐，没有或者为空文本的话就不加密
	 * @param salt String
	 * @return SimpleWebSocketClient
	 */
	fun salt(salt: String): SimpleWebSocketClient {
		this.salt = salt
		return this
	}

	fun onOpen(action: (ServerHandshake) -> Unit): SimpleWebSocketClient {
		onOpen = action
		return this
	}

	fun onClose(action: (code: Int, reason: String, remote: Boolean) -> Unit): SimpleWebSocketClient {
		onClose = action
		return this
	}

	fun onError(action: (Exception) -> Unit): SimpleWebSocketClient {
		onError = action
		return this
	}

	private var salt: String = ""

	private var onMessage: ((String) -> Unit)? = null

	private var onOpen: ((ServerHandshake) -> Unit)? = null

	private var onClose: ((code: Int, reason: String, remote: Boolean) -> Unit)? = null

	private var onError: ((Exception) -> Unit)? = {
		it.printStackTrace()
	}

	override fun onOpen(handshakedata: ServerHandshake) {
		println("[BakaMC(${name})]服务已连接")
		onOpen?.invoke(handshakedata)
	}

	override fun onMessage(message: String) {
		onMessage?.invoke(message)
	}

	override fun onClose(code: Int, reason: String, remote: Boolean) {
		println("[BakaMC(${name})]服务断开{code:$code,reason:$reason,remote:$remote}")
		onClose?.invoke(code, reason, remote)
	}

	override fun onError(ex: Exception) {
		println("[BakaMC(${name})]服务出错")
		onError?.invoke(ex)
	}
}