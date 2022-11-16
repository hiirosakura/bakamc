package cn.bakamc.riguru.config

import cn.bakamc.common.chat.config.ChatConfig
import cn.bakamc.common.config.common.CommonConfig
import cn.bakamc.common.config.common.TextConfig
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * riguru端的聊天配置

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.config

 * 文件名 ServerCommonConfig

 * 创建时间 2022/8/30 23:10

 * @author forpleuvoir

 */
object ServerCommonConfig : CommonConfig {
	override val chatConfig: ChatConfig
		get() = ServerChatConfig
	override val textConfig: TextConfig
		get() = ServerTextConfig

}

@ConfigurationProperties(prefix = "bakamc.chat")
object ServerChatConfig : ChatConfig {
	override lateinit var chatFormat: String
	override lateinit var whisperSenderFormat: String
	override lateinit var whisperReceiverFormat: String
	override lateinit var atFormat: String
	override lateinit var messageMapping: Map<String, String>
}

@ConfigurationProperties(prefix = "bakamc.text")
object ServerTextConfig : TextConfig {
	override lateinit var playerInfoHover: List<String>
	override lateinit var playerInfoClickCommand: String
	override lateinit var townHover: List<String>
	override lateinit var townClickCommand: String
	override lateinit var serverInfoClickCommand: String
}

