package cn.bakamc.common.chat

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType.CHAT_MESSAGE
import cn.bakamc.common.api.WSMessageType.CONFIG
import cn.bakamc.common.api.WSMessageType.REGISTRY_SERVER_INFO
import cn.bakamc.common.api.WSMessageType.WHISPER_MESSAGE
import cn.bakamc.common.chat.config.ChatConfig
import cn.bakamc.common.chat.config.RiguruChatConfig
import cn.bakamc.common.chat.message.Message
import cn.bakamc.common.chat.message.MessageChain
import cn.bakamc.common.chat.message.MessageType.Chat
import cn.bakamc.common.chat.message.MessageType.Whisper
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
					CHAT_MESSAGE, WHISPER_MESSAGE -> receivesMessage(gson.fromJson(data, MessageChain::class.java))
				}
			}
		} catch (e: Exception) {
			println("消息解析失败")
			e.printStackTrace()
		}
	}

	override lateinit var riguruChatConfig: RiguruChatConfig

	protected val messageHandlers: List<(Message) -> Message> = mutableListOf(
		//处理消息文本替换
		{ msg -> msg.apply { message = MessageUtil.handleFormat(message, riguruChatConfig.messageMapping) } },
		//处理@格式
		{ msg -> msg.apply { message = message.replace(Regex("@.+?(?<=\\b)")) { "§b${it.value}§r" } } }
	)

	override fun connect() {
		bakaChatWebSocketClient.connect()
		postMessage(WSMessage(CONFIG, ""))
	}

	override fun reconnect() = bakaChatWebSocketClient.reconnect()

	override fun close() = bakaChatWebSocketClient.close()

	override fun postMessage(message: WSMessage) = bakaChatWebSocketClient.send(message.toJsonStr())

	override fun Message.toChain(player: P): MessageChain {
		try {
			messageHandlers.forEach { it(this) }
		} catch (e: Exception) {
			println("消息预处理异常")
			e.printStackTrace()
		}
		handleItemShow(this, player)
		return MessageChain(type, sender, serverInfo, receiver, getAtList(), format())
	}

	/**
	 * 排序 文本
	 * @receiver Message
	 * @return List<String>
	 */
	protected open fun Message.format(): List<String> {
		return buildList {

		}
	}


	override fun receivesMessage(message: MessageChain) {
		when (message.type) {
			Whisper -> whisper(message)
			Chat    -> broadcast(message)
		}
	}

	override val MessageChain.text: T
		get() = TODO("Not yet implemented")

	override fun broadcast(message: MessageChain) {
		players.forEach { it.sendMessage(message.text) }
	}

	override fun whisper(message: MessageChain) {
		players.findLast {
			val p = it.info
			p.name == message.receiver && p.displayName == message.receiver
		}?.sendMessage(message.text)
	}

	/**
	 * 获取当前消息@的玩家名
	 * @param message Message
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
	 * 将[Message.message]转换为带json格式的物品信息Text的字符串  §({text})
	 * @param message Message
	 * @return Message
	 */
	open fun handleItemShow(message: Message, player: P): Message {
		val regex = Regex("%(\\d|i|o)")
		val itemTexts = ArrayList<T>()
		val text: T = "".text
		message.message.replace(regex) {
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
		message.message = text.toJson()
		return message
	}

	/**
	 * 将[index]位置的物品转换为Text [index]=-1时应该返回主手物品 [index]=-2返回副手的物品
	 * @param index Int
	 * @param player P
	 * @return String
	 */
	abstract fun P.getItemText(index: Int): T

}