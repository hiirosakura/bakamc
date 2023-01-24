package cn.bakamc.fabric.command


import cn.bakamc.fabric.BakaMC
import cn.bakamc.fabric.config.FabricCommonConfig
import cn.bakamc.fabric.config.FabricConfig
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.command

 * 文件名 ConfigCommand

 * 创建时间 2022/11/23 11:15

 * @author forpleuvoir

 */
object ConfigCommand {

	fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
		dispatcher.register(
			literal("bakamc:config").requires { it.hasPermissionLevel(4) }
				.then(literal("reload").executes {
					FabricConfig.load()
					FabricCommonConfig.init(FabricConfig.Server)
					it.source.sendFeedback(Text.of("§b[BakaMC]§a配置已重新加载"), false)
					1
				})
		)
		dispatcher.register(
			literal("bakamc:reload").requires { it.hasPermissionLevel(4) }
				.executes { context ->
					BakaMC.reload(context.source.server)
					context.source.sendFeedback(Text.of("§b[BakaMC]§aMod已重新加载"), false)
					1
				}
		)
	}
}