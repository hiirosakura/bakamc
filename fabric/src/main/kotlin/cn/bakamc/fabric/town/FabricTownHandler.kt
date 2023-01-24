package cn.bakamc.fabric.town

import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.town.TownHandler
import java.util.function.Consumer

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.town

 * 文件名 FabricTownHandler

 * 创建时间 2022/11/17 23:50

 * @author forpleuvoir

 */
class FabricTownHandler(config: ServerConfig) : TownHandler(config) {
	companion object {
		@JvmStatic
		lateinit var INSTANCE: TownHandler
			private set


		@JvmStatic
		fun hasHandler(action: Consumer<TownHandler>) {
			if (this::INSTANCE.isInitialized) {
				if (INSTANCE.isConnected()) action.accept(INSTANCE)
			}
		}

		@JvmStatic
		fun init(config: ServerConfig): TownHandler {
			INSTANCE = FabricTownHandler(config)
			INSTANCE.connect()
			return INSTANCE
		}
	}

}