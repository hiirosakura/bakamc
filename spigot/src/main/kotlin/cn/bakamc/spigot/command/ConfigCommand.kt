package cn.bakamc.spigot.command

import cn.bakamc.spigot.BakaMC
import cn.bakamc.spigot.chat.SpigotMessageHandler
import cn.bakamc.spigot.config.SpigotCommonConfig
import cn.bakamc.spigot.config.SpigotConfig
import cn.bakamc.spigot.town.SpigotTownManager
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandListenerWrapper

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.command

 * 文件名 ConfigCommand

 * 创建时间 2022/11/23 11:15

 * @author forpleuvoir

 */
object ConfigCommand {

	fun register(dispatcher: CommandDispatcher<CommandListenerWrapper>) {
		dispatcher.register(
			literal("bakamc:config").requires { it.bukkitSender.isOp }
				.then(literal("reload").executes {
					SpigotConfig.load()
					SpigotCommonConfig.init(SpigotConfig.Server)
					it.source.bukkitSender.sendMessage("§b[BakaMC]§a配置已重新加载")
					1
				})
		)
		dispatcher.register(
			literal("bakamc:reload").requires { it.bukkitSender.isOp }
				.executes { context ->
					SpigotConfig.load()
					SpigotCommonConfig.init(SpigotConfig.Server)
					SpigotMessageHandler.hasHandler { it.close() }
					SpigotTownManager.hasManager { it.close() }
					SpigotMessageHandler.init(SpigotConfig.Server, SpigotCommonConfig.INSTANCE, BakaMC.INSTANCE.server)
					SpigotTownManager.init(SpigotConfig.Server)
					context.source.bukkitSender.sendMessage("§b[BakaMC]§a插件已重新加载")
					1
				}
		)
	}
}