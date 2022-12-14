package cn.bakamc.common.chat.message

import cn.bakamc.common.chat.message.MessageType.Whisper
import cn.bakamc.common.common.ServerInfo
import cn.bakamc.common.player.PlayerCurrentInfo
import java.util.*

/**
 * 消息类

 * 项目名 bakamc

 * 包名 cn.bakamc.common.chat.message

 * 文件名 Message

 * 创建时间 2022/8/28 15:50

 * @author forpleuvoir

 */
open class Message(
	/**
	 * 消息类型
	 */
	val type: MessageType,
	/**
	 * 发送者信息
	 */
	val sender: PlayerCurrentInfo,
	/**
	 * 发送消息的服务器信息
	 */
	val serverInfo: ServerInfo,
	/**
	 * 如果是悄悄话 则为接受者的名称
	 */
	val receiver: String = "",
	/**
	 * 消息原始内容
	 */
	var message: String,
) {

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