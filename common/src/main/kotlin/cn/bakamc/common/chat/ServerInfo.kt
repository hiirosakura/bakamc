package cn.bakamc.common.chat

/**
 * 服务器信息

 * 项目名 bakamc

 * 包名 cn.bakamc.common.chat

 * 文件名 ServerInfo

 * 创建时间 2022/8/28 16:03

 * @author forpleuvoir

 */
class ServerInfo(
	/**
	 * 服务器ID
	 */
	val serverId: String,
	/**
	 * 服务器名称
	 */
	val serverName: String,
	/**
	 * 服务器描述
	 */
	val description: String,
) {
	override fun toString(): String {
		return "ServerInfo(serverId='$serverId', serverName='$serverName', description='$description')"
	}
}