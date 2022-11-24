package cn.bakamc.fabric

import cn.bakamc.fabric.command.Commands
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric

 * 文件名 BakaMC

 * 创建时间 2022/9/6 13:07

 * @author forpleuvoir

 */
object BakaMC : ModInitializer {

	const val ID = "bakamc"
	const val NAME = "BakaMC"

	override fun onInitialize() {
		CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess ->
			Commands.register(dispatcher)
		}
	}


}