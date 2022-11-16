package cn.bakamc.common.config.common

import cn.bakamc.common.api.serialization.JsonSerializer
import cn.bakamc.common.chat.config.ChatConfig
import cn.bakamc.common.utils.jsonObject
import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.common.config.common

 * 文件名 CommonConfig

 * 创建时间 2022/11/16 22:46

 * @author forpleuvoir

 */
interface CommonConfig : JsonSerializer {

	val chatConfig: ChatConfig

	val textConfig: TextConfig

	override val serialization: JsonElement
		get() = jsonObject {
			"chat_config" at chatConfig.serialization
			"text_config" at textConfig.serialization
		}


	override fun deserialize(serializedObject: JsonElement) {
		throw NotImplementedError("method not implemented")
	}

	companion object {
		@JvmStatic
		fun deserialize(serializedObject: JsonObject): CommonConfig = object : CommonConfig {
			override val chatConfig: ChatConfig
				get() = ChatConfig.deserialize(serializedObject.get("chat_config").asJsonObject)
			override val textConfig: TextConfig
				get() = TextConfig.deserialize(serializedObject.get("text_config").asJsonObject)

		}
	}
}