package cn.bakamc.common.config.common

import cn.bakamc.common.api.serialization.JsonSerializer
import cn.bakamc.common.utils.jsonArray
import cn.bakamc.common.utils.jsonObject
import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 *
 * 文本相关配置
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.common.config.common

 * 文件名 TextConfig

 * 创建时间 2022/11/16 22:18

 * @author forpleuvoir

 */
interface TextConfig : JsonSerializer {
	/**
	 * 玩家浮动信息显示
	 */
	val playerInfoHover: List<String>

	/**
	 * 点击玩家信息返回的指令建议
	 */
	val playerInfoClickCommand: String

	val townHover: List<String>

	val townClickCommand: String

	/**
	 * 点击服务器信息返回的指令建议
	 */
	val serverInfoClickCommand: String

	override val serialization: JsonElement
		get() = jsonObject {
			"player_info_hover" at jsonArray(playerInfoHover)
			"player_info_click_command" at playerInfoClickCommand
			"town_hover" at jsonArray(townHover)
			"town_click_command" at townClickCommand
			"server_info_click_command" at serverInfoClickCommand
		}

	override fun deserialize(serializedObject: JsonElement) {
		throw NotImplementedError("method not implemented")
	}

	companion object {
		@JvmStatic
		fun deserialize(serializedObject: JsonObject): TextConfig = object : TextConfig {
			override val playerInfoHover: List<String>
				get() = serializedObject.getAsJsonArray("player_info_hover").map { it.asString }
			override val playerInfoClickCommand: String
				get() = serializedObject.get("player_info_click_command").asString
			override val townHover: List<String>
				get() = serializedObject.getAsJsonArray("town_hover").map { it.asString }
			override val townClickCommand: String
				get() = serializedObject.get("town_click_command").asString
			override val serverInfoClickCommand: String
				get() = serializedObject.get("server_info_click_command").asString
		}
	}
}