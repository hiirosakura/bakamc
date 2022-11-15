package cn.bakamc.common.config.common

import cn.bakamc.common.common.ServerInfo

/**
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

	val serverInfo: ServerInfo get() = ServerInfo(serverId, serverName, description)

	/**
	 * riguru服务器地址
	 */
	val riguruAddress: String

	fun reload()
}