package cn.bakamc.common.chat

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType
import cn.bakamc.common.chat.config.ChatConfig
import cn.bakamc.common.chat.message.Message
import cn.bakamc.common.chat.message.MessageType.Chat
import cn.bakamc.common.chat.message.MessageType.Whisper
import cn.bakamc.common.chat.message.PostMessage
import cn.bakamc.common.common.MultiPlatform
import cn.bakamc.common.common.ServerInfo
import cn.bakamc.common.config.common.CommonConfig
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.utils.toJsonStr
import java.util.*

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
interface MessageHandler<T, P, S> : MultiPlatform<T, P, S> {

	/**
	 * 配置
	 */
	val config: ServerConfig

	val commonConfig: CommonConfig

	/**
	 * 从服务端获取的配置
	 */
	val chatConfig: ChatConfig get() = commonConfig.chatConfig


	/**
	 * 服务器信息
	 */
	val serverInfo: ServerInfo get() = config.serverInfo

	/**
	 * 服务器实力类
	 */
	val server: S

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
	fun sendChatMessage(player: P, message: String)

	/**
	 * 当前服务器玩家发布私聊消息给指定玩家
	 * @param player P
	 * @param message String
	 * @param receiver String
	 */
	fun sendWhisperMessage(player: P, message: String, receiver: String)

	/**
	 * 消息发送前的预处理
	 */
	fun Message.toFinalMessage(player: P): PostMessage

	/**
	 * 向当前服务器指定玩家发送消息
	 * @receiver P
	 * @param message Text[T]
	 */
	fun P.sendMessage(message: T, uuid: UUID)

	/**
	 * 向riguru服务器发送消息
	 * @param message Message
	 */
	fun postMessage(message: WSMessage)

	/**
	 * 当从riguru服务器接收到消息时
	 * @param message Message
	 */
	fun receivesMessage(message: PostMessage)

	/**
	 * 向服务器广播消息
	 * @param message Message
	 */
	fun broadcast(message: PostMessage)

	/**
	 * 向指定玩家发送悄悄话
	 * @param message Message
	 */
	fun whisper(message: PostMessage)


}