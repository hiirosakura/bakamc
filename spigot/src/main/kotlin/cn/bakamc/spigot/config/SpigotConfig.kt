package cn.bakamc.spigot.config

import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.config.modconfig.impl.ConfigCategoryImpl
import cn.bakamc.common.config.modconfig.impl.LocalServerModConfig
import cn.bakamc.spigot.BakaMC
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.file.Path


/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.config

 * 文件名 SpigotServerConfig

 * 创建时间 2022/11/22 20:35

 * @author forpleuvoir

 */
object SpigotConfig : LocalServerModConfig<JavaPlugin>(BakaMC.ID) {
	object Server : ConfigCategoryImpl("server", this), ServerConfig {

		@JvmStatic
		val SERVER_ID = configString("server_id", "sur")

		@JvmStatic
		val SERVER_NAME = configString("server_name", "§c生存服")

		@JvmStatic
		val DESCRIPTION = configStringList(
			"description",
			listOf(
				"服务器ID : %serverID%\n",
				"服务器名 ：%serverName%\n",
				"这里是描述文本\n",
				"点击加入到该服务器"
			)
		)

		@JvmStatic
		val RIGURU_WEB_SOCKET_ADDRESS = configString("riguru_web_socket_address", "ws://127.0.0.1:3499")

		@JvmStatic
		val RIGURU_HTTP_ADDRESS = configString("riguru_http_address", "http://127.0.0.1:3499")

		override val serverId: String
			get() = SERVER_ID.getValue()

		override val serverName: String
			get() = SERVER_NAME.getValue()

		override val description: List<String>
			get() = DESCRIPTION.getValue()

		override val riguruWebSocketAddress: String
			get() = RIGURU_WEB_SOCKET_ADDRESS.getValue()

		override val riguruHttpAddress: String
			get() = RIGURU_HTTP_ADDRESS.getValue()

		override fun reload() {
			SpigotConfig.loadAsync()
		}
	}

	override fun localConfigPath(): Path {
		var jarWholePath: String = this::class.java.protectionDomain.codeSource.location.file
		try {
			jarWholePath = URLDecoder.decode(jarWholePath, "UTF-8")
		} catch (e: UnsupportedEncodingException) {
			e.printStackTrace()
		}
		val jarPath = File(jarWholePath).parentFile.absolutePath
		val file = File(jarPath, "BakaMC")
		if (!file.exists()) {
			file.mkdir()
		}
		return file.toPath()
	}

	override lateinit var server: JavaPlugin

}