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
	 * 聊天格式
	 */
	val chatFormatting: String

	/**
	 * 细聊发送者消息格式
	 */
	val whisperSenderFormatting: String

	/**
	 * 私聊接受者 消息格式
	 */
	val whisperReceiverFormatting: String

	/**
	 * 聊天文本替换
	 */
	val messageMapping: Map<String, String>


	override val serialization: JsonElement
		get() = jsonObject {
			"chatFormatting" at chatFormatting
			"whisperSenderFormatting" at whisperSenderFormatting
			"whisperReceiverFormatting" at whisperReceiverFormatting
			"messageMapping" at jsonObject(messageMapping)
		}

	override fun deserialize(serializedObject: JsonElement) {
		throw NotImplementedError("方法未实现")
	}

	companion object {
		@JvmStatic
		fun deserialize(serializedObject: JsonObject): RiguruChatConfig = object : RiguruChatConfig {
			override val chatFormatting: String
				get() = serializedObject.get("chatFormatting").asString
			override val whisperSenderFormatting: String
				get() = serializedObject.get("whisperSenderFormatting").asString
			override val whisperReceiverFormatting: String
				get() = serializedObject.get("whisperReceiverFormatting").asString
			override val messageMapping: Map<String, String>
				get() = buildMap { serializedObject.getAsJsonObject("messageMapping").entrySet().forEach { this[it.key] = it.value.asString } }
		}
	}
}