package cn.bakamc.spigot

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

	override fun onEnable() {
		server.onlinePlayers.forEach {
			it.spigot().sendMessage()
		}
	}

	override fun onDisable() {

	}

}