package cn.bakamc.riguru.config

import cn.bakamc.common.chat.config.ChatConfig
import cn.bakamc.common.config.common.CommonConfig
import cn.bakamc.common.config.common.TextConfig
import cn.bakamc.common.config.modconfig.impl.ConfigCategoryImpl
import cn.bakamc.common.config.modconfig.impl.LocalModConfig
import cn.bakamc.riguru.RiguruApplication
import org.slf4j.LoggerFactory
import org.springframework.boot.system.ApplicationHome
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.nio.file.Path
import kotlin.io.path.Path

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.config

 * 文件名 RiguruConfig

 * 创建时间 2022/11/29 11:56

 * @author forpleuvoir

 */
@EnableScheduling
@Component
object RiguruConfig : LocalModConfig("riguru"), CommonConfig {

	private val log = LoggerFactory.getLogger(RiguruConfig::class.java)

	@Scheduled(fixedRate = 60000, initialDelay = 10000)
	fun autoSave() {
		if (RiguruConfig.needSave) {
			log.info("[Riguru]配置保存中...")
			RiguruConfig.saveAsync()
		}
	}

	object Riguru : ConfigCategoryImpl("riguru", this) {

		@JvmStatic
		val SECRET = configString("secret", "")

	}

	object Chat : ConfigCategoryImpl("chat", this), ChatConfig {

		val CHAT_FORMAT = configString("chat_format", "#{§6[§rserverName§6]}#{§6[§rtownShortName§6]}#{§6[§r§bplayerName§6]}§b:§r #{message}")

		val WHISPER_SENDER_FORMAT = configString("whisper_sender_format", "§7你悄悄对#{§6[§r§7receiver§6]}§7说§b:§r #{message}")

		val WHISPER_RECEIVER_FORMAT = configString(
			"whisper_receiver_format",
			"#{§6[§rserverName§6]}#{§6[§rtownShortName§6]}#{§6[§r§bplayerName§6]}§7悄悄对你说§b:§r #{message}"
		)

		val AT_FORMAT = configString("at_format", "§b%at%§r")

		val MESSAGE_MAPPING = configStringMap(
			"message_mapping", mapOf(
				"&0" to "§0",
				"&1" to "§1",
				"&2" to "§2",
				"&3" to "§3",
				"&4" to "§4",
				"&5" to "§5",
				"&6" to "§6",
				"&7" to "§7",
				"&8" to "§8",
				"&9" to "§9",
				"&a" to "§a",
				"&b" to "§b",
				"&c" to "§c",
				"&d" to "§d",
				"&e" to "§e",
				"&f" to "§f",
				"&g" to "§g",
				"&h" to "§h",
				"&u" to "§u",
				"&l" to "§l",
				"&o" to "§o",
				"&m" to "§m",
				"&k" to "§k",
				"&r" to "§r",
			)
		)

		override val chatFormat: String
			get() = CHAT_FORMAT.getValue()
		override val whisperSenderFormat: String
			get() = WHISPER_SENDER_FORMAT.getValue()
		override val whisperReceiverFormat: String
			get() = WHISPER_RECEIVER_FORMAT.getValue()
		override val atFormat: String
			get() = AT_FORMAT.getValue()
		override val messageMapping: Map<String, String>
			get() = MESSAGE_MAPPING.getValue()

	}

	object Text : ConfigCategoryImpl("text", this), TextConfig {

		val PLAYER_INFO_HOVER = configStringList(
			"player_info_hover", listOf(
				"玩家名称 : %name%\n",
				"玩家显示名 : %displayName%\n",
				"UUID : %uuid%\n",
				"所在小镇 : %town%\n",
				"玩家等级 : %level%\n",
				"玩家总经验 : %experience%\n",
				"生命值 : %health%/%maxHealth%\n",
				"所在维度 : %dimension%\n",
				"点击私聊"
			)
		)

		val PLAYER_INFO_CLICK_COMMAND = configString("player_info_click_command", "/bakamc:chat whisper %name% ")

		val TOWN_HOVER = configStringList(
			"town_hover", listOf(
				"小镇ID : %id%\n",
				"小镇名称 : %name%\n",
				"小镇简称 : %shortName%\n",
				"创建时间 : %createTime%\n",
				"镇长 : %mayor%\n",
				"管理员 : %admin%\n",
				"成员 : %member%\n",
				"点击申请加入该小镇"
			)
		)

		val TOWN_CLICK_COMMAND = configString("town_click_command", "/bakamc:town application %name% ")

		val SERVER_INFO_CLICK_COMMAND = configString("server_info_click_command", "/server %serverID%")
		override val playerInfoHover: List<String>
			get() = PLAYER_INFO_HOVER.getValue()
		override val playerInfoClickCommand: String
			get() = PLAYER_INFO_CLICK_COMMAND.getValue()
		override val townHover: List<String>
			get() = TOWN_HOVER.getValue()
		override val townClickCommand: String
			get() = TOWN_CLICK_COMMAND.getValue()
		override val serverInfoClickCommand: String
			get() = SERVER_INFO_CLICK_COMMAND.getValue()

	}

	init {
//		addCategory(Riguru)
//		addCategory(Chat)
//		addCategory(Text)
	}

	override fun localConfigPath(): Path {
		return Path(ApplicationHome(RiguruApplication::class.java).source.parentFile.path)
	}

	override val chatConfig: ChatConfig
		get() = Chat
	override val textConfig: TextConfig
		get() = Text
}