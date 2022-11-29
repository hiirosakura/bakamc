package cn.bakamc.spigot.config

import cn.bakamc.common.config.common.AbstractServerConfig
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
	object Server : AbstractServerConfig("server", this) {
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