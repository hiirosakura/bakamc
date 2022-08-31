package cn.bakamc.common.chat.message

import cn.bakamc.common.chat.PlayerInfo
import cn.bakamc.common.chat.ServerInfo

/**
 * 最终被发送的消息

 * 项目名 bakamc

 * 包名 cn.bakamc.common.chat.message

 * 文件名 MessageChain

 * 创建时间 2022/8/31 0:54

 * @author forpleuvoir

 */
class PostMessage(
	/**
	 * 消息类型
	 */
	type: MessageType,
	/**
	 * 发送者信息
	 */
	sender: PlayerInfo,
	/**
	 * 发送消息的服务器信息
	 */
	serverInfo: ServerInfo,
	/**
	 * 如果是悄悄话 则为接受者的名称
	 */
	receiver: String = "",
	/**
	 * 消息原始内容
	 */
	message: String,
	/**
	 * 数据包括消息内容以及@列表
	 *
	 * finalText : [String] 普通消息
	 *
	 * finalSenderText : [String] 若为私聊消息则为发送者看到的格式
	 *
	 * finalReceiverText : [String] 若为私聊消息则为接受者看到的格式
	 *
	 * atList : [List] 被@的玩家列表
	 */
	val data: Map<String, Any>
) : Message(type, sender, serverInfo, receiver, message) {

	val finalText: String? get() = data[FINAL_TEXT] as String?

	fun <T> finalText(converter: String.() -> T): T? = finalText?.let(converter)

	val finalSenderText: String? get() = data[FINAL_SENDER_TEXT] as String?

	fun <T> finalSenderText(converter: String.() -> T): T? = finalSenderText?.let(converter)

	val finalReceiverText: String? get() = data[FINAL_RECEIVER_TEXT] as String?

	fun <T> finalReceiverText(converter: String.() -> T): T? = finalReceiverText?.let(converter)

	val atList: List<String>? get() = data[AT_LIST] as List<String>?

	fun <T> atList(converter: String.() -> T): List<T>? =
		atList?.let { list ->
			buildList {
				list.forEach { add(converter(it)) }
			}
		}

	companion object {
		const val FINAL_TEXT = "finalText"
		const val FINAL_SENDER_TEXT = "finalSenderText"
		const val FINAL_RECEIVER_TEXT = "finalReceiverText"
		const val AT_LIST = "atList"
	}
}