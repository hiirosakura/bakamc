package cn.bakamc.spigot.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.CommandListenerWrapper
import net.minecraft.server.dedicated.DedicatedServer
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.CompletableFuture


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
			dedicatedServer.vanillaCommandDispatcher.a().apply {
				ChatCommand.register(this)
				ConfigCommand.register(this)

			}
		} catch (e: NoSuchMethodException) {
			e.printStackTrace()
		} catch (e: IllegalAccessException) {
			e.printStackTrace()
		} catch (e: InvocationTargetException) {
			e.printStackTrace()
		}
	}
}

fun literal(name: String): LiteralArgumentBuilder<CommandListenerWrapper> {
	return LiteralArgumentBuilder.literal(name)
}

fun <T> argument(name: String, type: ArgumentType<T>): RequiredArgumentBuilder<CommandListenerWrapper, T> {
	return RequiredArgumentBuilder.argument(name, type)
}

fun suggest(iterable: Iterable<String>, suggestionsBuilder: SuggestionsBuilder): CompletableFuture<Suggestions> {
	val string = suggestionsBuilder.remaining.lowercase()
	for (string2 in iterable) {
		if (!matchesSubStr(string, string2.lowercase())) continue
		suggestionsBuilder.suggest(string2)
	}
	return suggestionsBuilder.buildFuture()
}

fun matchesSubStr(string: String?, string2: String): Boolean {
	var i = 0
	while (!string2.startsWith(string!!, i)) {
		if (string2.indexOf(95.toChar(), i).also { i = it } < 0) {
			return false
		}
		++i
	}
	return true
}