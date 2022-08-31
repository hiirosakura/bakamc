package cn.bakamc.common.chat.config

import cn.bakamc.common.api.serialization.JsonSerializer
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
	 * @的格式
	 */
	val atFormat:String

	/**
	 * 聊天文本替换
	 */
	val messageMapping: Map<String, String>


	override val serialization: JsonElement
		get() = jsonObject {
			"chatFormat" at chatFormat
			"whisperSenderFormat" at whisperSenderFormat
			"whisperReceiverFormat" at whisperReceiverFormat
			"atFormat" at atFormat
			"messageMapping" at jsonObject(messageMapping)
		}

	override fun deserialize(serializedObject: JsonElement) {
		throw NotImplementedError("方法未实现")
	}

	companion object {
		@JvmStatic
		fun deserialize(serializedObject: JsonObject): RiguruChatConfig = object : RiguruChatConfig {
			override val chatFormat: String
				get() = serializedObject.get("chatFormat").asString
			override val whisperSenderFormat: String
				get() = serializedObject.get("whisperSenderFormat").asString
			override val whisperReceiverFormat: String
				get() = serializedObject.get("whisperReceiverFormat").asString
			override val atFormat: String
				get() = serializedObject.get("atFormat").asString
			override val messageMapping: Map<String, String>
				get() = buildMap { serializedObject.getAsJsonObject("messageMapping").entrySet().forEach { this[it.key] = it.value.asString } }
		}
	}
}