package cn.bakamc.common.chat

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType.Chat.CHAT_MESSAGE
import cn.bakamc.common.api.WSMessageType.Chat.WHISPER_MESSAGE
import cn.bakamc.common.chat.message.Message
import cn.bakamc.common.chat.message.MessageType.Chat
import cn.bakamc.common.chat.message.MessageType.Whisper
import cn.bakamc.common.chat.message.PostMessage
import cn.bakamc.common.chat.message.PostMessage.Companion.AT_LIST
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
	override val multiplatform: MultiPlatform<T, P, S>,
	override val commonConfig: CommonConfig
) : MessageHandler<T, P, S> {

	protected val webSocketClient = SimpleWebSocketClient("${config.riguruWebSocketAddress}/chat/${config.serverInfo.serverID}") {
		try {
			gson.fromJson(it, WSMessage::class.java).run {
				when (type) {
					CHAT_MESSAGE, WHISPER_MESSAGE -> receivesMessage(gson.fromJson(data, PostMessage::class.java))
				}
			}
		} catch (e: Exception) {
			println("消息解析失败")
			e.printStackTrace()
		}
	}

	protected val messageHandlers: MutableList<(String, player: P) -> String> = mutableListOf(
		//处理消息文本替换
		{ msg, _ -> MessageUtil.handleFormat(msg, chatConfig.messageMapping) },
		//处理@格式
		{ msg, _ -> msg.replace(Regex("@.+?(?<=\\b)")) { chatConfig.atFormat.replace("%at%", it.value) } },
		//处理之后将变为可直接反序列化为对应环境的Text的Json文本 最好是放在最后添加
		::handleItemShow
	)

	override fun connect() {
		webSocketClient.connect()
	}

	override fun reconnect() = webSocketClient.reconnect()

	override fun close() = webSocketClient.close()

	override fun postMessage(message: WSMessage) {
		webSocketClient.send(message.toJsonStr())
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
			"serverName" to { multiplatform.serverNameText(serverInfo, it) },
			"serverID" to { multiplatform.serverIdText(serverInfo, it) },
			"playerName" to { multiplatform.playerNameText(sender, it) },
			"playerDisplayName" to { multiplatform.playerDisplayNameText(sender, it) },
			"townName" to { multiplatform.townNameText(sender.town, it) },
			"townShortName" to { multiplatform.townShortNameText(sender.town, it) },
			"message" to { multiplatform.textFromJson(msg) },
			"receiver" to { multiplatform.stringToText(it.replace("receiver", receiver)) }
		)
		val data = buildMap {
			when (type) {
				Chat    ->
					this[FINAL_TEXT] = parse(chatConfig.chatFormat, formatHandlers).toJson()

				Whisper -> {
					this[FINAL_SENDER_TEXT] = parse(chatConfig.whisperSenderFormat, formatHandlers).toJson()
					this[FINAL_RECEIVER_TEXT] = parse(chatConfig.whisperReceiverFormat, formatHandlers).toJson()
				}
			}
			this[AT_LIST] = getAtList()
		}
		return PostMessage(type, sender, serverInfo, receiver, message, data)
	}


	protected open fun Message.parse(msgForMatting: String, formatHandlers: Map<String, (String) -> T>): T {
		val finalText: T = "".text
		val texts = ArrayList<T>()
		val regex = Regex("#\\{.+?}")
		val toEmpty = arrayOf("#{", "}")
		msgForMatting.replace(regex) {
			val str = it.value.replace(toEmpty, "")
			texts.add(
				run {
					var text = str.text
					formatHandlers.forEach { (k, v) ->
						if (str.contains(k)) text = v(str)
					}
					text
				}
			)
			it.value
		}.split(regex).toMutableList().apply {
			finalText.addSiblings(this[0].text)
			texts.forEachIndexed { index, t ->
				finalText.addSiblings(t)
				finalText.addSiblings(this[index + 1].text)
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
		multiplatform.players(server).forEach { it.sendMessage(message.finalText { multiplatform.textFromJson(this) }!!, message.senderUUID) }
	}

	override fun whisper(message: PostMessage) {
		multiplatform.players(server).findLast {
			val p = multiplatform.playerCurrentInfo(it)
			p.name == message.receiver && p.displayName == message.receiver
		}?.sendMessage(message.finalReceiverText { multiplatform.textFromJson(this) }!!, message.senderUUID)
		multiplatform.players(server).findLast {
			val p = multiplatform.playerCurrentInfo(it)
			p.uuid == message.senderUUID
		}?.sendMessage(message.finalSenderText { multiplatform.textFromJson(this) }!!, message.senderUUID)
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
		val text: T = "".text
		message.replace(regex) {
			val indexChar = it.value[1].toString()
			val index = if (indexChar == "i") -1 else if (indexChar == "o") -2 else indexChar.toInt() - 1
			itemTexts.add(player.getItemText(index))
			it.value
		}.split(regex).toMutableList().apply {
			text.addSiblings(this[0].text)
			itemTexts.forEachIndexed { index, s ->
				text.addSiblings(s)
				text.addSiblings(this[index + 1].text)
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