package cn.bakamc.fabric.config

import cn.bakamc.common.config.common.AbstractServerConfig
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.config.modconfig.impl.ConfigCategoryImpl
import cn.bakamc.common.config.modconfig.impl.LocalServerModConfig
import cn.bakamc.fabric.BakaMC
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
object FabricConfig : LocalServerModConfig<MinecraftServer>(BakaMC.ID) {

	object Server : AbstractServerConfig("server", this) {
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