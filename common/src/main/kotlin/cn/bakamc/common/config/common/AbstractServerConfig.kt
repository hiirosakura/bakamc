package cn.bakamc.common.config.common

import cn.bakamc.common.config.modconfig.ModConfig
import cn.bakamc.common.config.modconfig.impl.ConfigCategoryImpl

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.common.config.common

 * 文件名 AbstractServerConfig

 * 创建时间 2022/11/28 15:53

 * @author forpleuvoir

 */
abstract class AbstractServerConfig(name: String, modConfig: ModConfig) : ConfigCategoryImpl(name, modConfig), ServerConfig {

	val SERVER_ID = configString("server_id", "sur")

	val SERVER_NAME = configString("server_name", "§c生存服")

	val DESCRIPTION = configStringList(
		"description",
		listOf(
			"服务器ID : %serverID%\n",
			"服务器名 ：%serverName%\n",
			"这里是描述文本\n",
			"点击加入到该服务器"
		)
	)

	val CHAT_ACROSS_SERVERS = configBoolean("chat_across_servers", true)

	val RIGURU_SECRET = configString("riguru_secret", "")

	val RIGURU_WEB_SOCKET_ADDRESS = configString("riguru_web_socket_address", "ws://127.0.0.1:3499")

	val RIGURU_HTTP_ADDRESS = configString("riguru_http_address", "http://127.0.0.1:3499")

	override val serverId: String
		get() = SERVER_ID.getValue()

	override val serverName: String
		get() = SERVER_NAME.getValue()

	override val description: List<String>
		get() = DESCRIPTION.getValue()

	override val chatAcrossServers: Boolean
		get() = CHAT_ACROSS_SERVERS.getValue()
	override val riguruSecret: String
		get() = RIGURU_SECRET.getValue()

	override
	val riguruWebSocketAddress: String
		get() = RIGURU_WEB_SOCKET_ADDRESS.getValue()

	override val riguruHttpAddress: String
		get() = RIGURU_HTTP_ADDRESS.getValue()

}