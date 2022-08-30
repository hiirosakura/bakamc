package cn.bakamc.common.chat

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType
import cn.bakamc.common.chat.config.ChatConfig
import cn.bakamc.common.chat.config.RiguruChatConfig
import cn.bakamc.common.chat.message.Message
import cn.bakamc.common.chat.message.MessageChain
import cn.bakamc.common.chat.message.MessageType.Chat
import cn.bakamc.common.chat.message.MessageType.Whisper
import cn.bakamc.common.utils.toJsonStr

/**
 * 消息处理器
 *
 * 所有处理应该在发送服务器处理完成，发送给riguru的必须是可以直接反序列化的消息链

 * 项目名 bakamc

 * 包名 cn.bakamc.common.chat

 * 文件名 MessageHandler

 * 创建时间 2022/8/28 17:33

 * @author forpleuvoir

 */
interface MessageHandler<T, P> {

	/**
	 * 配置
	 */
	val config: ChatConfig

	/**
	 * 从服务端获取的配置
	 */
	val riguruChatConfig: RiguruChatConfig

	/**
	 * 服务器信息
	 */
	val serverInfo: ServerInfo get() = config.serverInfo

	/**
	 * 重新加载配置
	 */
	fun reloadConfig() = config.reload()

	/**
	 * 链接riguru服务器
	 */
	fun connect()

	/**
	 * 重新连接至riguru服务器
	 */
	fun reconnect()

	/**
	 * 关闭连接
	 */
	fun close()

	/**
	 * 当前服务器玩家发布聊天信息
	 * @param player P
	 * @param message String
	 */
	fun sendChatMessage(player: P, message: String) {
		postMessage(
			WSMessage(
				WSMessageType.CHAT_MESSAGE,
				Message(Chat, player.info, serverInfo, "", message).toChain(player).toJsonStr()
			)
		)
	}

	/**
	 * 当前服务器玩家发布私聊消息给指定玩家
	 * @param player P
	 * @param message String
	 * @param receiver String
	 */
	fun sendWhisperMessage(player: P, message: String, receiver: String) {
		postMessage(
			WSMessage(
				WSMessageType.WHISPER_MESSAGE,
				Message(Whisper, player.info, serverInfo, receiver, message).toChain(player).toJsonStr()
			)
		)
	}

	/**
	 * 消息发送前的预处理
	 */
	fun Message.toChain(player: P): MessageChain

	/**
	 * 向当前服务器指定玩家发送消息
	 * @receiver P
	 * @param message Text[T]
	 */
	fun P.sendMessage(message: T)

	/**
	 * 向riguru服务器发送消息
	 * @param message Message
	 */
	fun postMessage(message: WSMessage)

	/**
	 * 当从riguru服务器接收到消息时
	 * @param message Message
	 */
	fun receivesMessage(message: MessageChain)

	/**
	 * 向服务器广播消息
	 * @param message Message
	 */
	fun broadcast(message: MessageChain)

	/**
	 * 向指定玩家发送悄悄话
	 * @param message Message
	 */
	fun whisper(message: MessageChain)

	/**
	 * 在当前文本后添加文本
	 * @receiver T
	 * @param text T
	 * @return T
	 */
	fun T.append(text: T): T

	/**
	 * 将Text转换为可解析的Json文本
	 * @receiver T
	 * @return String
	 */
	fun T.toJson(): String

	/**
	 * 将Json文本解析为当前环境的Text
	 * @receiver String
	 * @return T
	 */
	fun String.fromJson(): T

	/**
	 * 将字符串转换为当前环境下的Text
	 */
	val String.text: T

	/**
	 * 获取玩家信息
	 */
	val P.info: PlayerInfo

	/**
	 * 将玩家转换为对应环境的Text
	 */
	val PlayerInfo.text: T

	/**
	 * 将服务器信息转换为对应环境的Text
	 */
	val ServerInfo.text: T

	/**
	 * 将消息转换为当前环境对应的Text
	 */
	val MessageChain.text: T

	/**
	 * 获取当前服务器所有在线的玩家
	 */
	val players: Iterable<P>
}