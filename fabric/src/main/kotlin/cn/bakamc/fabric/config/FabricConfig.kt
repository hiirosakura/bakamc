package cn.bakamc.fabric.config

import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.config.modconfig.impl.ConfigCategoryImpl
import cn.bakamc.common.config.modconfig.impl.LocalServerModConfig
import cn.bakamc.fabric.BakaMc
import net.minecraft.server.MinecraftServer
import java.io.File
import java.nio.file.Path

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.chat.config

 * 文件名 FabricConfig

 * 创建时间 2022/9/6 13:32

 * @author forpleuvoir

 */
object FabricConfig : LocalServerModConfig<MinecraftServer>(BakaMc.ID) {

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
			FabricConfig.loadAsync()
		}
	}

	override fun localConfigPath(): Path {
		val serverPath = server.session.directory
		return File(serverPath.toFile(), modId).apply {
			if (!exists()) {
				this.mkdir()
			}
		}.toPath()
	}

	override lateinit var server: MinecraftServer

}