package cn.bakamc.common.chat.message

/**
 * 消息类型

 * 项目名 bakamc

 * 包名 cn.bakamc.common.chat.message

 * 文件名 MessageType

 * 创建时间 2022/8/28 15:51

 * @author forpleuvoir

 */
enum class MessageType(val desc: String) {
	Chat("普通消息"), Whisper("悄悄话")
}