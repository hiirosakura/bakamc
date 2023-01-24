package cn.bakamc.spigot

import cn.bakamc.common.BakaMcApp
import cn.bakamc.spigot.chat.SpigotMessageHandler
import cn.bakamc.spigot.command.Commands
import cn.bakamc.spigot.config.SpigotCommonConfig
import cn.bakamc.spigot.config.SpigotConfig
import cn.bakamc.spigot.event.PlayerEventHandler
import cn.bakamc.spigot.player.SpigotPlayerHandler
import cn.bakamc.spigot.town.SpigotTownHandler
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot

 * 文件名 BakaMC

 * 创建时间 2022/11/22 11:14

 * @author forpleuvoir

 */
class BakaMC : JavaPlugin(), BakaMcApp<Server> {

	companion object {
		const val ID = "bakamc"
		lateinit var INSTANCE: BakaMC
	}

	private fun registerEvent() {
		server.pluginManager.apply {
			registerEvents(PlayerEventHandler, this@BakaMC)
		}
	}

	override fun onEnable() {
		INSTANCE = this
		init(server)
		Commands.registerCommand(this)
		registerEvent()
	}

	override fun onDisable() {
		close(server)
	}

	override fun init(server: Server) {
		SpigotConfig.init(this)
		SpigotCommonConfig.init(SpigotConfig.Server)
		SpigotMessageHandler.init(SpigotConfig.Server, SpigotCommonConfig.INSTANCE, server)
		SpigotTownHandler.init(SpigotConfig.Server)
		SpigotPlayerHandler.init(SpigotConfig.Server, SpigotCommonConfig.INSTANCE, server)
	}

	override fun reload(server: Server) {
		SpigotConfig.load()
		SpigotCommonConfig.init(SpigotConfig.Server)
		SpigotMessageHandler.hasHandler { it.close() }
		SpigotTownHandler.hasHandler { it.close() }
		SpigotPlayerHandler.hasHandler { it.close() }
		SpigotMessageHandler.init(SpigotConfig.Server, SpigotCommonConfig.INSTANCE, server)
		SpigotTownHandler.init(SpigotConfig.Server)
		SpigotPlayerHandler.init(SpigotConfig.Server, SpigotCommonConfig.INSTANCE, server)
	}

	override fun close(server: Server) {
		SpigotConfig.save()
		SpigotMessageHandler.hasHandler { it.close() }
		SpigotTownHandler.hasHandler { it.close() }
		SpigotPlayerHandler.hasHandler { it.close() }
	}

}