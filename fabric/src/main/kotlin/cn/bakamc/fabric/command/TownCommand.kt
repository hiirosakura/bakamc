package cn.bakamc.fabric.command

import cn.bakamc.fabric.town.FabricTownManager
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.command

 * 文件名 TownCommand

 * 创建时间 2022/11/23 13:10

 * @author forpleuvoir

 */
object TownCommand {

	fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
		dispatcher.register(
			literal("bakamc:town")
				.then(
					literal("syncData").requires { it.hasPermissionLevel(4) }
						.executes {
							FabricTownManager.hasManager { it.syncData() }
							1
						}
				)
		)
	}


}