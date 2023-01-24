package cn.bakamc.spigot.command

import cn.bakamc.spigot.town.SpigotTownHandler
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandListenerWrapper

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.command

 * 文件名 TownCommand

 * 创建时间 2022/11/23 13:10

 * @author forpleuvoir

 */
object TownCommand {

	fun register(dispatcher: CommandDispatcher<CommandListenerWrapper>) {
		dispatcher.register(
			literal("bakamc:town")
				.then(
					literal("syncData").requires { it.bukkitSender.isOp }
						.executes {
							SpigotTownHandler.hasHandler{it.syncData()}
							1
						}
				)
		)
	}

}