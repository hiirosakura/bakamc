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
	 * 普通消息格式
	 */
	val chatFormat: String

	/**
	 * 细聊发送者消息格式
	 */
	val whisperSenderFormat: String

	/**
	 * 私聊接受者 消息格式
	 */
	val whisperReceiverFormat: String

	/**
	 * @ 的格式
	 */
	val atFormat:String

	/**
	 * 玩家浮动信息显示
	 */
	val playerInfoHover:List<String>

	/**
	 * 点击玩家信息返回的指令建议
	 */
	val playerInfoClickCommand:String

	/**
	 * 点击服务器信息返回的指令建议
	 */
	val serverInfoClickCommand:String

	/**
	 * 聊天文本替换
	 */
	val messageMapping: Map<String, String>


	override val serialization: JsonElement
		get() = jsonObject {
			"chat_format" at chatFormat
			"whisper_sender_format" at whisperSenderFormat
			"whisper_receiver_format" at whisperReceiverFormat
			"at_format" at atFormat
			"player_info_hover" at jsonArray(playerInfoHover)
			"player_info_click_command" at playerInfoClickCommand
			"server_info_click_command" at serverInfoClickCommand
			"message_mapping" at jsonObject(messageMapping)
		}

	override fun deserialize(serializedObject: JsonElement) {
		throw NotImplementedError("method not implemented")
	}

	companion object {
		@JvmStatic
		fun deserialize(serializedObject: JsonObject): RiguruChatConfig = object : RiguruChatConfig {
			override val chatFormat: String
				get() = serializedObject.get("chat_format").asString
			override val whisperSenderFormat: String
				get() = serializedObject.get("whisper_sender_format").asString
			override val whisperReceiverFormat: String
				get() = serializedObject.get("whisper_receiver_format").asString
			override val atFormat: String
				get() = serializedObject.get("at_format").asString
			override val playerInfoHover: List<String>
				get() = buildList { serializedObject.getAsJsonArray("player_info_hover").forEach { add(it.asString) } }
			override val playerInfoClickCommand: String
				get() = serializedObject.get("player_info_click_command").asString
			override val serverInfoClickCommand: String
				get() = serializedObject.get("server_info_click_command").asString
			override val messageMapping: Map<String, String>
				get() = buildMap { serializedObject.getAsJsonObject("message_mapping").entrySet().forEach { this[it.key] = it.value.asString } }
		}
	}
}