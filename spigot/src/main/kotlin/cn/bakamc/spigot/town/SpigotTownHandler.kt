package cn.bakamc.spigot.town

import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.town.TownHandler
import java.util.function.Consumer

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.town

 * 文件名 SpigotTownHandler

 * 创建时间 2022/11/22 21:19

 * @author forpleuvoir

 */
class SpigotTownHandler(config: ServerConfig) : TownHandler(config) {
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
			INSTANCE = SpigotTownHandler(config)
			INSTANCE.connect()
			return INSTANCE
		}
	}

}