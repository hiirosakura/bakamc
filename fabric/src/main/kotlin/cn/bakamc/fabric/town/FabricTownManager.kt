package cn.bakamc.fabric.town

import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.town.TownManager
import java.util.function.Consumer

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.town

 * 文件名 FabricTownManager

 * 创建时间 2022/11/17 23:50

 * @author forpleuvoir

 */
class FabricTownManager(config: ServerConfig) : TownManager(config) {
	companion object {
		@JvmStatic
		lateinit var INSTANCE: TownManager
			private set


		@JvmStatic
		fun hasManager(action: Consumer<TownManager>) {
			if (this::INSTANCE.isInitialized) {
				action.accept(INSTANCE)
			}
		}

		@JvmStatic
		fun init(config: ServerConfig): TownManager {
			INSTANCE = FabricTownManager(config)
			INSTANCE.connect()
			return INSTANCE
		}
	}

}