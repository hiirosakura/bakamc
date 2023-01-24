package cn.bakamc.common.api

import org.java_websocket.client.WebSocketClient

interface WebSocketHandler {

	val webSocketClient: WebSocketClient

	fun connect() = webSocketClient.connect()

	fun isConnected(): Boolean = webSocketClient.isOpen

	fun reconnect() = webSocketClient.reconnect()

	fun close() = webSocketClient.close()

}