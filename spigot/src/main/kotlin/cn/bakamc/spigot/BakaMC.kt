package cn.bakamc.spigot

import cn.bakamc.spigot.chat.SpigotMessageHandler
import cn.bakamc.spigot.command.Commands
import cn.bakamc.spigot.config.SpigotCommonConfig
import cn.bakamc.spigot.config.SpigotConfig
import cn.bakamc.spigot.event.PlayerEventHandler
import cn.bakamc.spigot.player.SpigotPlayerManager
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
		lateinit var INSTANCE: JavaPlugin
	}

	private fun registerEvent() {
		server.pluginManager.apply {
			registerEvents(PlayerEventHandler, this@BakaMC)
		}
	}

	override fun onEnable() {
		INSTANCE = this
		SpigotConfig.init(this)
		SpigotCommonConfig.init(SpigotConfig.Server)
		SpigotMessageHandler.init(SpigotConfig.Server, SpigotCommonConfig.INSTANCE, server)
		SpigotTownManager.init(SpigotConfig.Server)
		SpigotPlayerManager.init(SpigotConfig.Server, SpigotCommonConfig.INSTANCE, server)
		Commands.registerCommand(this)
		registerEvent()
	}

	override fun onDisable() {
		SpigotConfig.save()
		SpigotMessageHandler.hasHandler { it.close() }
		SpigotTownManager.hasManager { it.close() }
		SpigotPlayerManager.hasManager { it.close() }
	}

}