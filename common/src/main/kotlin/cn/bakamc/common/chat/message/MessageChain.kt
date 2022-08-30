package cn.bakamc.common.chat.message

import cn.bakamc.common.chat.PlayerInfo
import cn.bakamc.common.chat.ServerInfo

/**
 * 消息链

 * 项目名 bakamc

 * 包名 cn.bakamc.common.chat.message

 * 文件名 MessageChain

 * 创建时间 2022/8/31 0:54

 * @author forpleuvoir

 */
class MessageChain(
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
	 * 被@的玩家列表
	 */
	val atList:List<String>,
	/**
	 * 消息链
	 */
	val msgChain:List<String>,
) {
}