package cn.bakamc.common.chat.message

import cn.bakamc.common.chat.PlayerInfo
import cn.bakamc.common.chat.ServerInfo
import cn.bakamc.common.chat.message.MessageType.Whisper
import cn.bakamc.common.utils.gson
import com.google.gson.JsonElement
import java.util.*

/**
 * 消息类

 * 项目名 bakamc

 * 包名 cn.bakamc.common.chat.message

 * 文件名 Message

 * 创建时间 2022/8/28 15:50

 * @author forpleuvoir

 */
class Message(
	/**
	 * 消息类型
	 */
	val type: MessageType,
	/**
	 * 发送者信息
	 */
	val sender: PlayerInfo,
	/**
	 * 发送消息的服务器信息
	 */
	val serverInfo: ServerInfo,
	/**
	 * 如果是悄悄话 则为接受者的名称
	 */
	val receiver: String = "",
	/**
	 * 消息内容
	 *
	 * §(content§)为占位符 内容将解析为 text
	 */
	val message: String,
	/**
	 * 序列化之后的消息内容，可以被转换为对应环境的消息内容
	 */
	var msg:String
) {
	companion object {
		@JvmStatic
		fun fromJsonStr(json: String): Message {
			return gson.fromJson(json, Message::class.java)
		}

		@JvmStatic
		fun fromJson(json: JsonElement): Message {
			return gson.fromJson(json, Message::class.java)
		}
	}

	val senderUUID: UUID
		get() = sender.uuid

	/**
	 * 是否为悄悄话
	 */
	val isWhisper: Boolean
		get() = type == Whisper && receiver.isNotEmpty()

	override fun toString(): String {
		return "Message(type=$type, sender=$sender, serverInfo=$serverInfo, receiver='$receiver', message='$message')"
	}

}