package cn.bakamc.common.chat

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType.CHAT_MESSAGE
import cn.bakamc.common.api.WSMessageType.CONFIG
import cn.bakamc.common.api.WSMessageType.REGISTRY_SERVER_INFO
import cn.bakamc.common.api.WSMessageType.WHISPER_MESSAGE
import cn.bakamc.common.chat.config.ChatConfig
import cn.bakamc.common.chat.config.RiguruChatConfig
import cn.bakamc.common.chat.message.Message
import cn.bakamc.common.chat.message.PostMessage
import cn.bakamc.common.chat.message.MessageType.Chat
import cn.bakamc.common.chat.message.MessageType.Whisper
import cn.bakamc.common.chat.message.PostMessage.Companion.AT_LIST
import cn.bakamc.common.chat.message.PostMessage.Companion.FINAL_RECEIVER_TEXT
import cn.bakamc.common.chat.message.PostMessage.Companion.FINAL_SENDER_TEXT
import cn.bakamc.common.chat.message.PostMessage.Companion.FINAL_TEXT
import cn.bakamc.common.utils.MessageUtil
import cn.bakamc.common.utils.gson
import cn.bakamc.common.utils.parseToJsonObject
import cn.bakamc.common.utils.toJsonStr
import java.util.*
import kotlin.collections.ArrayList

/**
 * 消息处理器基础实现

 * 项目名 bakamc

 * 包名 cn.bakamc.common.chat

 * 文件名 AbstractMessageHandler

 * 创建时间 2022/8/29 15:58

 * @author forpleuvoir

 */
abstract class AbstractMessageHandler<T, P>(
	final override val config: ChatConfig
) : MessageHandler<T, P> {

	protected val bakaChatWebSocketClient = BakaChatWebSocketClient("${config.riguruAddress}/chat/${config.serverInfo.serverId}") {
		try {
			gson.fromJson(it, WSMessage::class.java).run {
				when (type) {
					CONFIG                        -> riguruChatConfig = RiguruChatConfig.deserialize(this.data.parseToJsonObject)
					REGISTRY_SERVER_INFO          -> println(data)
					CHAT_MESSAGE, WHISPER_MESSAGE -> receivesMessage(gson.fromJson(data, PostMessage::class.java))
				}
			}
		} catch (e: Exception) {
			println("消息解析失败")
			e.printStackTrace()
		}
	}

	override lateinit var riguruChatConfig: RiguruChatConfig

	protected val messageHandlers: MutableList<(String, player: P) -> String> = mutableListOf(
		//处理消息文本替换
		{ msg, _ -> MessageUtil.handleFormat(msg, riguruChatConfig.messageMapping) },
		//处理@格式
		{ msg, _ -> msg.replace(Regex("@.+?(?<=\\b)")) { riguruChatConfig.atFormat.replace("%at%", it.value) } },
		//处理之后将变为可直接反序列化为对应环境的Text的Json文本 最好是放在最后添加
		::handleItemShow
	)

	override fun connect() {
		bakaChatWebSocketClient.connect()
		postMessage(WSMessage(CONFIG, ""))
	}

	override fun reconnect() = bakaChatWebSocketClient.reconnect()

	override fun close() = bakaChatWebSocketClient.close()

	override fun postMessage(message: WSMessage) = bakaChatWebSocketClient.send(message.toJsonStr())

	override fun Message.toFinalMessage(player: P): PostMessage {
		var msg: String = message
		try {
			messageHandlers.forEach { msg = it(message, player) }
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
		val regex = Regex("%(§[0-9a-gulomkr])*(server|playerName|playerDisplayName|town|message|receiver)%")
		val toEmpty = Regex("%")
		val data = buildMap {
			when (type) {
				Chat    ->
					this[FINAL_TEXT] = format(regex, toEmpty, riguruChatConfig.chatFormat).toJson()

				Whisper -> {
					this[FINAL_SENDER_TEXT] = format(regex, toEmpty, riguruChatConfig.whisperSenderFormat).toJson()
					this[FINAL_RECEIVER_TEXT] = format(regex, toEmpty, riguruChatConfig.whisperReceiverFormat).toJson()
				}
			}
			this[AT_LIST] = getAtList()
		}
		return PostMessage(type, sender, serverInfo, receiver, message, data)
	}


	protected open fun Message.format(regex: Regex, toEmpty: Regex, msgForMatting: String): T {
		val finalText: T = "".text
		val texts = ArrayList<T>()
		msgForMatting.replace(regex) {
			val str = it.value.replace(toEmpty, "")
			texts.add(
				when {
					str == "server"                                             -> serverInfo.text
					str == "playerName"                                         -> sender.text
					str == "playerDisplayName"                                  -> sender.displayNameText
					str == "town"                                               -> sender.townText
					"(§[0-9a-gulomkr])*receiver".toRegex().containsMatchIn(str) -> str.replace(receiver, "receiver").text
					else                                                        -> str.text
				}
			)
			it.value
		}.split(regex).toMutableList().apply {
			texts.forEachIndexed { index, t ->
				finalText.append(t)
				finalText.append(this[index + 1].text)
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
		players.forEach { it.sendMessage(message.finalText { this.fromJson() }!!) }
	}

	override fun whisper(message: PostMessage) {
		players.findLast {
			val p = it.info
			p.name == message.receiver && p.displayName == message.receiver
		}?.sendMessage(message.finalReceiverText { this.fromJson() }!!)
		players.findLast {
			val p = it.info
			p.uuid == message.senderUUID
		}?.sendMessage(message.finalSenderText { this.fromJson() }!!)
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
			itemTexts.forEachIndexed { index, s ->
				text.append(s)
				text.append(this[index + 1].text)
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