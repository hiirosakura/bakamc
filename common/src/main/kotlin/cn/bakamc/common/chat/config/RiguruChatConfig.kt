package cn.bakamc.common.chat.config

import cn.bakamc.common.api.serialization.JsonSerializer
import cn.bakamc.common.utils.jsonArray
import cn.bakamc.common.utils.jsonObject
import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * 聊天系统服务端配置

 * 项目名 bakamc

 * 包名 cn.bakamc.common.chat.config

 * 文件名 RiguruChatConfig

 * 创建时间 2022/8/30 23:06

 * @author forpleuvoir

 */
interface RiguruChatConfig : JsonSerializer {

	/**
	 * 聊天顺序 server:服务器 player:玩家名 :message
	 */
	val chatFormatting: List<String>

	/**
	 * 聊天顺序 server:服务器 player:玩家名 :message
	 */
	val whisperFormatting: List<String>

	/**
	 * 服务器名包装器
	 *
	 * 占位符
	 *
	 * 服务器名 %serverName%
	 */
	val serverWrapper: String

	/**
	 * 玩家名包装器
	 *
	 * 占位符
	 *
	 * 玩家名 %playerName%
	 *
	 * 玩家显示名 %displayName%
	 */
	val playerNameWrapper: String

	/**
	 * 消息包装器
	 *
	 * 占位符
	 *
	 * 消息内容 %message%
	 */
	val messageWrapper: String

	/**
	 * 聊天文本替换
	 */
	val messageMapping: Map<String, String>


	override val serialization: JsonElement
		get() = jsonObject {
			"chatFormatting" at jsonArray(chatFormatting)
			"whisperFormatting" at jsonArray(whisperFormatting)
			"serverWrapper" at serverWrapper
			"playerNameWrapper" at playerNameWrapper
			"messageWrapper" at messageWrapper
			"messageMapping" at jsonObject(messageMapping)
		}

	override fun deserialize(serializedObject: JsonElement) {
		throw NotImplementedError("方法未实现")
	}

	companion object {
		@JvmStatic
		fun deserialize(serializedObject: JsonObject): RiguruChatConfig = object : RiguruChatConfig {
			override val chatFormatting: List<String>
				get() = buildList { serializedObject.getAsJsonArray("chatFormatting").forEach { add(it.asString) } }
			override val whisperFormatting: List<String>
				get() = buildList { serializedObject.getAsJsonArray("whisperFormatting").forEach { add(it.asString) } }
			override val serverWrapper: String
				get() = serializedObject.getAsJsonPrimitive("serverWrapper").asString
			override val playerNameWrapper: String
				get() = serializedObject.getAsJsonPrimitive("playerNameWrapper").asString
			override val messageWrapper: String
				get() = serializedObject.getAsJsonPrimitive("messageWrapper").asString
			override val messageMapping: Map<String, String>
				get() = buildMap { serializedObject.getAsJsonObject("messageMapping").entrySet().forEach { this[it.key] = it.value.asString } }
		}
	}
}