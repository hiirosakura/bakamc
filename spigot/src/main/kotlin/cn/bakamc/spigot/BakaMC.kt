package cn.bakamc.spigot

import cn.bakamc.spigot.chat.SpigotMessageHandler
import cn.bakamc.spigot.command.Commands
import cn.bakamc.spigot.config.SpigotCommonConfig
import cn.bakamc.spigot.config.SpigotConfig
import cn.bakamc.spigot.event.MessageEventHandler
import cn.bakamc.spigot.town.SpigotTownManager
import org.bukkit.plugin.java.JavaPlugin

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot

 * 文件名 BakaMC

 * 创建时间 2022/11/22 11:14

 * @author forpleuvoir

 */
class BakaMC : JavaPlugin() {

	companion object {
		const val ID = "bakamc"
	}

	private fun registerEvent() {
		server.pluginManager.apply {
			registerEvents(MessageEventHandler, this@BakaMC)
		}
	}

	override fun onEnable() {
		SpigotConfig.init(this)
		SpigotCommonConfig.init(SpigotConfig.Server)
		SpigotMessageHandler.init(SpigotConfig.Server, SpigotCommonConfig.INSTANCE, server)
		SpigotTownManager.init(SpigotConfig.Server)
		Commands.registerCommand(this)
		registerEvent()
	}

	override fun onDisable() {
		SpigotConfig.saveAsync()
		SpigotMessageHandler.hasHandler { it.close() }
		SpigotTownManager.hasManager { it.close() }
	}

}