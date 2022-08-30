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

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as ServerInfo

		if (serverId != other.serverId) return false
		if (serverName != other.serverName) return false
		if (description != other.description) return false

		return true
	}

	override fun hashCode(): Int {
		return serverId.hashCode()
	}
}