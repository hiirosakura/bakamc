package cn.bakamc.riguru.config

import cn.bakamc.common.chat.config.RiguruChatConfig
import com.google.gson.JsonElement
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * riguru端的聊天配置

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.config

 * 文件名 ServerChatConfig

 * 创建时间 2022/8/30 23:10

 * @author forpleuvoir

 */
@ConfigurationProperties(prefix = "bakamc.chat")
object ServerChatConfig : RiguruChatConfig {
	override lateinit var chatFormat: String
	override lateinit var whisperSenderFormat: String
	override lateinit var whisperReceiverFormat: String
	override lateinit var atFormat: String
	override lateinit var messageMapping: Map<String, String>
}
