package cn.bakamc.fabric.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.ServerCommandSource

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.command

 * 文件名 Commands

 * 创建时间 2022/11/22 22:57

 * @author forpleuvoir

 */
object Commands {

	fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
		ConfigCommand.register(dispatcher)
		ChatCommand.register(dispatcher)
		TownCommand.register(dispatcher)
	}

}