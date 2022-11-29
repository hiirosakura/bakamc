package cn.bakamc.common.config.common

import cn.bakamc.common.common.ServerInfo

/**
 *
 * 当前服务器终端配置
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.common.config.common

 * 文件名 ServerConfig

 * 创建时间 2022/11/15 23:39

 * @author forpleuvoir

 */
interface ServerConfig {

	/**
	 * 当前服务器ID
	 */
	val serverId: String

	/**
	 * 当前服务器名称
	 */
	val serverName: String

	/**
	 * 当前服务器描述
	 */
	val description: List<String>

	/**
	 * 是否启用全服聊天
	 */
	val chatAcrossServers: Boolean

	val serverInfo: ServerInfo get() = ServerInfo(serverId, serverName, description)

	val riguruSecret: String

	/**
	 * riguru WebSocket服务器地址
	 */
	val riguruWebSocketAddress: String

	/**
	 * riguru HTTP服务器地址
	 */
	val riguruHttpAddress: String

	fun reload()
}