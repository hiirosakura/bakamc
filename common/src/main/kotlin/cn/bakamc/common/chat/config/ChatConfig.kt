package cn.bakamc.common.chat.config

import cn.bakamc.common.common.ServerInfo

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

	val description: List<String>

	val serverInfo: ServerInfo get() = ServerInfo(serverId, serverName, description)

	val riguruAddress: String

	/**
	 * 重新加载配置
	 */
	fun reload()
}