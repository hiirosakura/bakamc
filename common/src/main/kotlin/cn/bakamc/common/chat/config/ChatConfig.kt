package cn.bakamc.common.chat.config

import cn.bakamc.common.chat.ServerInfo
import cn.bakamc.common.utils.MessageUtil

/**
 * 聊天配置

 * 项目名 bakamc

 * 包名 cn.bakamc.common.chat.config

 * 文件名 ChatConfig

 * 创建时间 2022/8/28 16:54

 * @author forpleuvoir

 */
interface ChatConfig {

	val serverId: String

	val serverName: String

	val description: String

	val serverInfo: ServerInfo get() = ServerInfo(serverId, serverName, description)

	val riguruAddress: String

	/**
	 * 聊天顺序 server:服务器 prefix:玩家前缀 playerName:玩家名 3:message
	 */
	val chatFormatting: String

	/**
	 * 服务器名包装器
	 *
	 * 占位符
	 *
	 * 服务器名 %serverName%
	 */
	val serverWrapper: String

	/**
	 * 玩家名包装器
	 *
	 * 占位符
	 *
	 * 玩家名 %playerName%
	 *
	 * 玩家显示名 %displayName%
	 */
	val playerNameWrapper: String

	/**
	 * 消息包装器
	 *
	 * 占位符
	 *
	 * 消息内容 %message%
	 */
	val messageWrapper: String

	/**
	 * 聊天文本替换
	 */
	val messageMapping: Map<String, String>

	/**
	 * 重新加载配置
	 */
	fun reload()
}