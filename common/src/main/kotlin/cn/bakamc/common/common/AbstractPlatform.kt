package cn.bakamc.common.common

import cn.bakamc.common.chat.config.ChatConfig
import cn.bakamc.common.config.common.CommonConfig
import cn.bakamc.common.config.common.TextConfig
import cn.bakamc.common.town.Town
import cn.bakamc.common.utils.format
import cn.bakamc.common.utils.replace
import java.math.RoundingMode.HALF_UP
import java.text.DecimalFormat

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.common.common

 * 文件名 AbstractPlatform

 * 创建时间 2022/11/17 0:11

 * @author forpleuvoir

 */
abstract class AbstractPlatform<T, P, S>(protected val commonConfig: CommonConfig) : MultiPlatform<T, P, S> {
	protected val chatConfig: ChatConfig get() = commonConfig.chatConfig

	protected val textConfig: TextConfig get() = commonConfig.textConfig

	protected val String.text: T get() = stringToText(this)

	protected fun T.addSiblings(sibling: T): T = addSiblings(this, sibling)

	abstract fun displayText(display: T, hoverText: T? = null, command: String? = null): T

	override fun playerNameText(playerCurrentInfo: PlayerCurrentInfo, origin: String): T {
		val text = origin.replace("name", playerCurrentInfo.name).text
		val hoverText = "".text
		textConfig.playerInfoHover.placeholderHandler(playerCurrentInfo).forEach { hoverText.addSiblings(it.text) }
		val command = textConfig.playerInfoClickCommand.replace(playerCurrentInfo.placeholder)
		return displayText(text, hoverText, command)
	}

	override fun playerDisplayNameText(playerCurrentInfo: PlayerCurrentInfo, origin: String): T {
		val text = origin.replace("displayName", playerCurrentInfo.displayName).text
		val hoverText = "".text
		textConfig.playerInfoHover.placeholderHandler(playerCurrentInfo).forEach { hoverText.addSiblings(it.text) }
		val command = textConfig.playerInfoClickCommand.replace(playerCurrentInfo.placeholder)
		return displayText(text, hoverText, command)
	}

	override fun townNameText(town: Town, origin: String): T {
		if (town == Town.NONE) return "".text
		val text = origin.replace("name", town.name).text
		val hoverText = "".text
		textConfig.townHover.placeholderHandler(town).forEach { hoverText.addSiblings(it.text) }
		val command = textConfig.townClickCommand.replace(town.placeholder)
		return displayText(text, hoverText, command)
	}

	override fun townShortNameText(town: Town, origin: String): T {
		if (town == Town.NONE) return "".text
		val text = origin.replace("name", town.shortName).text
		val hoverText = "".text
		textConfig.townHover.placeholderHandler(town).forEach { hoverText.addSiblings(it.text) }
		val command = textConfig.townClickCommand.replace(town.placeholder)
		return displayText(text, hoverText, command)
	}

	override fun serverNameText(serverInfo: ServerInfo, origin: String): T {
		val text = origin.replace("serverName", serverInfo.serverName).text
		val hoverText = "".text
		serverInfo.description.placeholderHandler(serverInfo).forEach { hoverText.addSiblings(it.text) }
		val command = textConfig.serverInfoClickCommand.replace(serverInfo.placeholder)
		return displayText(text, hoverText, command)
	}

	override fun serverIdText(serverInfo: ServerInfo, origin: String): T {
		val text = origin.replace("serverID", serverInfo.serverID).text
		val hoverText = "".text
		serverInfo.description.placeholderHandler(serverInfo).forEach { hoverText.addSiblings(it.text) }
		val command = textConfig.serverInfoClickCommand.replace(serverInfo.placeholder)
		return displayText(text, hoverText, command)
	}

	protected val PlayerCurrentInfo.placeholder: Map<String, String>
		get() {
			val format = DecimalFormat("#.##")
			format.roundingMode = HALF_UP
			return buildMap {
				this["%name%"] = this@placeholder.name
				this["%displayName%"] = this@placeholder.displayName
				this["%uuid%"] = this@placeholder.uuid.toString()
				this["%town%"] = this@placeholder.town.name
				this["%level%"] = this@placeholder.level.toString()
				this["%experience%"] = this@placeholder.experience.toString()
				this["%maxHealth%"] = format.format(this@placeholder.maxHealth)
				this["%health%"] = format.format(this@placeholder.health)
				this["%dimension%"] = this@placeholder.dimension
			}
		}

	protected fun List<String>.placeholderHandler(playerInfo: PlayerCurrentInfo): List<String> = buildList {
		this@placeholderHandler.forEach { add(it.replace(playerInfo.placeholder)) }
	}

	protected val Town.placeholder: Map<String, String>
		get() {
			return buildMap {
				this["%id%"] = this@placeholder.id.toString()
				this["%name%"] = this@placeholder.name
				this["%shortName%"] = this@placeholder.shortName
				this["%createTime%"] = this@placeholder.formatTime()
				this["%mayor%"] = this@placeholder.mayor.first.name
				this["%admin%"] = this@placeholder.admin.map { it.name }.format(5, prefix = "[", suffix = "]")
				this["%member%"] = this@placeholder.member.map { it.name }.format(5, prefix = "[", suffix = "]")
			}
		}

	protected fun List<String>.placeholderHandler(town: Town): List<String> = buildList {
		this@placeholderHandler.forEach { add(it.replace(town.placeholder)) }
	}

	protected val ServerInfo.placeholder: Map<String, String>
		get() {
			return buildMap {
				this["%serverID%"] = serverID
				this["%serverName%"] = serverName
			}
		}

	protected fun List<String>.placeholderHandler(serverInfo: ServerInfo): List<String> = buildList {
		this@placeholderHandler.forEach { add(it.replace(serverInfo.placeholder)) }
	}
}