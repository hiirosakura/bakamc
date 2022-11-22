package cn.bakamc.spigot.command

import net.minecraft.server.dedicated.DedicatedServer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.command

 * 文件名 Commands

 * 创建时间 2022/11/22 21:38

 * @author forpleuvoir

 */
object Commands {
	fun registerCommand(plugin: JavaPlugin) {
		val craftServerClass: Class<*> = Bukkit.getServer().javaClass
		try {
			val method: Method = craftServerClass.getDeclaredMethod("getServer")
			val dedicatedServer: DedicatedServer = method.invoke(Bukkit.getServer()) as DedicatedServer
			val commandDispatcher = dedicatedServer.vanillaCommandDispatcher.a()
			ChatCommand.register(commandDispatcher)
		} catch (e: NoSuchMethodException) {
			e.printStackTrace()
		} catch (e: IllegalAccessException) {
			e.printStackTrace()
		} catch (e: InvocationTargetException) {
			e.printStackTrace()
		}
//		ChatCommand.registerCommand(plugin)
	}
}

