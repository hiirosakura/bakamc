package cn.bakamc.fabric.config

import cn.bakamc.common.chat.config.ChatConfig
import cn.bakamc.common.config.common.CommonConfig
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.config.common.TextConfig
import cn.bakamc.common.utils.net.httpGet
import cn.bakamc.common.utils.parseToJsonObject

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.config

 * 文件名 FabricCommonConfig

 * 创建时间 2022/11/16 23:05

 * @author forpleuvoir

 */
class FabricCommonConfig(serverConfig: ServerConfig) : CommonConfig {

	companion object {

		@JvmStatic
		lateinit var INSTANCE: CommonConfig
			private set

		@JvmStatic
		fun init(serverConfig: ServerConfig): CommonConfig {
			INSTANCE = FabricCommonConfig(serverConfig)
			return INSTANCE
		}
	}

	init {
		val body = httpGet("${serverConfig.riguruHttpAddress}/common_config")
			.sendGetBody()
		CommonConfig.deserialize(body.parseToJsonObject).let {
			chatConfig = it.chatConfig
			textConfig = it.textConfig
		}
	}

	override var chatConfig: ChatConfig
	override var textConfig: TextConfig
}