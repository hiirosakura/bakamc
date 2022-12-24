package cn.bakamc.common.chat

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType.Chat.CHAT_MESSAGE
import cn.bakamc.common.api.WSMessageType.Chat.WHISPER_MESSAGE
import cn.bakamc.common.chat.message.Message
import cn.bakamc.common.chat.message.MessageType.Chat
import cn.bakamc.common.chat.message.MessageType.Whisper
import cn.bakamc.common.chat.message.PostMessage
import cn.bakamc.common.chat.message.PostMessage.Companion.AT_LIST
import cn.bakamc.common.chat.message.PostMessage.Companion.FINAL_MESSAGE
import cn.bakamc.common.chat.message.PostMessage.Companion.FINAL_RECEIVER_TEXT
import cn.bakamc.common.chat.message.PostMessage.Companion.FINAL_SENDER_TEXT
import cn.bakamc.common.chat.message.PostMessage.Companion.FINAL_TEXT
import cn.bakamc.common.common.MultiPlatform
import cn.bakamc.common.common.SimpleWebSocketClient
import cn.bakamc.common.config.common.CommonConfig
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.utils.MessageUtil
import cn.bakamc.common.utils.gson
import cn.bakamc.common.utils.replace
import cn.bakamc.common.utils.toJsonStr
import java.util.*

/**
 * 消息处理器基础实现

 * 项目名 bakamc

 * 包名 cn.bakamc.common.chat

 * 文件名 AbstractMessageHandler

 * 创建时间 2022/8/29 15:58

 * @author forpleuvoir

 */
abstract class AbstractMessageHandler<T, P, S>(
	final override val config: ServerConfig,
	override val commonConfig: CommonConfig
) : MessageHandler<T, P, S>, MultiPlatform<T, P, S> {

	protected open val webSocketClient =
		SimpleWebSocketClient("chat", "${config.riguruWebSocketAddress}/chat/${config.serverInfo.serverID}")
			.onMessage { if (config.chatAcrossServers) this.onMessage(it) }
			.salt(config.riguruSecret)

	protected open val messageHandlers: MutableList<(String, player: P) -> String> = mutableListOf(
		//处理消息文本替换
		{ msg, _ -> MessageUtil.handleFormat(msg, chatConfig.messageMapping) },
		//处理@格式
		{ msg, _ -> msg.replace(Regex("@.+?(?<=\\b)")) { chatConfig.atFormat.replace("%at%", it.value) } },
		//处理之后将变为可直接反序列化为对应环境的Text的Json文本 最好是放在最后添加
		::handleItemShow
	)

	protected open fun onMessage(message: WSMessage) {
		when (message.type) {
			CHAT_MESSAGE, WHISPER_MESSAGE -> receivesMessage(gson.fromJson(message.data, PostMessage::class.java))
		}
	}

	override fun connect() {
		webSocketClient.connect()
	}

	override fun reconnect() = webSocketClient.reconnect()

	override fun close() = webSocketClient.close()

	override fun sendChatMessage(player: P, message: String) {
		postMessage(
			WSMessage(
				CHAT_MESSAGE,
				Message(Chat, player.playerCurrentInfo(), serverInfo, "", message).toFinalMessage(player).toJsonStr()
			)
		)
	}

	override fun sendWhisperMessage(player: P, message: String, receiver: String) {
		postMessage(
			WSMessage(
				WHISPER_MESSAGE,
				Message(Whisper, player.playerCurrentInfo(), serverInfo, receiver, message).toFinalMessage(player).toJsonStr()
			)
		)
	}

	override fun postMessage(message: WSMessage) {
		if (config.chatAcrossServers)
			webSocketClient.send(message)
		else
			onMessage(message)
	}

	override fun Message.toFinalMessage(player: P): PostMessage {
		var msg: String = message
		try {
			messageHandlers.forEach { msg = it(msg, player) }
		} catch (e: Exception) {
			println("消息预处理异常")
			e.printStackTrace()
		}
		return format(msg)
	}

	/**
	 * 转换为对应格式
	 * @receiver [Message]
	 * @param msg [String]
	 * @return [String]
	 */
	protected open fun Message.format(msg: String): PostMessage {
		val formatHandlers = mapOf<String, (String) -> T>(
			"serverName" to { serverInfo.nameText(it) },
			"serverID" to { serverInfo.idText(it) },
			"playerName" to { sender.nameText(it) },
			"playerDisplayName" to { sender.displayNameText(it) },
			"townName" to { sender.town.nameText(it) },
			"townShortName" to { sender.town.shortNameText(it) },
			"message" to { msg.fromJson() },
			"receiver" to { it.replace("receiver", receiver).toText() }
		)
		val data = buildMap {
			when (type) {
				Chat -> {
					val finalText = parse(chatConfig.chatFormat, formatHandlers)
					this[FINAL_TEXT] = finalText.toJson()
					this[FINAL_MESSAGE] = MessageUtil.cleanFormatting(finalText.toPlain())
				}

				Whisper -> {
					this[FINAL_SENDER_TEXT] = parse(chatConfig.whisperSenderFormat, formatHandlers).toJson()
					this[FINAL_RECEIVER_TEXT] = parse(chatConfig.whisperReceiverFormat, formatHandlers).toJson()
					this[FINAL_MESSAGE] = MessageUtil.cleanFormatting(
						serverInfo.nameText()
							.addSibling("=>".toText())
							.addSibling("[$receiver]: ".toText())
							.addSibling(msg.fromJson())
							.toPlain()
					)
				}
			}
			this[AT_LIST] = getAtList()
		}
		return PostMessage(type, sender, serverInfo, receiver, message, data)
	}


	protected open fun Message.parse(msgForMatting: String, formatHandlers: Map<String, (String) -> T>): T {
		val finalText: T = "".toText()
		val texts = ArrayList<T>()
		val regex = Regex("#\\{.+?}")
		val toEmpty = arrayOf("#{", "}")
		msgForMatting.replace(regex) {
			val str = it.value.replace(toEmpty, "")
			texts.add(
				run {
					var text = str.toText()
					formatHandlers.forEach { (k, v) ->
						if (str.contains(k)) text = v(str)
					}
					text
				}
			)
			it.value
		}.split(regex).toMutableList().apply {
			finalText.addSibling(this[0].toText())
			texts.forEachIndexed { index, t ->
				finalText.addSibling(t)
				finalText.addSibling(this[index + 1].toText())
			}
		}
		return finalText
	}

	override fun receivesMessage(message: PostMessage) {
		when (message.type) {
			Whisper -> whisper(message)
			Chat    -> broadcast(message)
		}
	}

	override fun broadcast(message: PostMessage) {
		server.players().forEach { it.sendMessage(message.finalText { this.fromJson() }!!, message.senderUUID) }
	}

	override fun whisper(message: PostMessage) {
		server.players().findLast {
			val p = it.playerCurrentInfo()
			p.name == message.receiver && p.displayName == message.receiver
		}?.sendMessage(message.finalReceiverText { this.fromJson() }!!, message.senderUUID)
		server.players().findLast {
			val p = it.playerCurrentInfo()
			p.uuid == message.senderUUID
		}?.sendMessage(message.finalSenderText { this.fromJson() }!!, message.senderUUID)
	}

	/**
	 * 获取当前消息@的玩家名
	 * @receiver [Message]
	 * @return List<String>
	 */
	protected open fun Message.getAtList(): List<String> {
		val list = LinkedList<String>()
		this.message.replace(Regex("@.+?\\b")) {
			list.add(it.value.substring(1))
			it.value
		}
		return list
	}

	/**
	 * 将消息内容转换为带json格式的物品信息Text的字符串
	 * @param message [String]
	 * @param player [P]
	 * @return Message
	 */
	open fun handleItemShow(message: String, player: P): String {
		val regex = Regex("%(\\d|i|o)")
		val itemTexts = ArrayList<T>()
		val text: T = "".toText()
		message.replace(regex) {
			val indexChar = it.value[1].toString()
			val index = if (indexChar == "i") -1 else if (indexChar == "o") -2 else indexChar.toInt() - 1
			itemTexts.add(player.getItemText(index))
			it.value
		}.split(regex).toMutableList().apply {
			text.addSibling(this[0].toText())
			itemTexts.forEachIndexed { index, s ->
				text.addSibling(s)
				text.addSibling(this[index + 1].toText())
			}
		}
		return text.toJson()
	}

	/**
	 * 将[index]位置的物品转换为Text [index]=-1时应该返回主手物品 [index]=-2返回副手的物品
	 * @receiver [P]
	 * @param index Int
	 * @return String
	 */
	abstract fun P.getItemText(index: Int): T

}