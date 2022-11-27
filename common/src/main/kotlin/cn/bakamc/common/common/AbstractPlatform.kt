package cn.bakamc.common.common

import cn.bakamc.common.common.MultiPlatform.ClickAction.SUGGEST_COMMAND
import cn.bakamc.common.common.MultiPlatform.HoverAction.SHOW_TEXT
import cn.bakamc.common.config.common.TextConfig
import cn.bakamc.common.player.PlayerCurrentInfo
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
interface AbstractPlatform<T, P, S> : MultiPlatform<T, P, S> {

	val textConfig: TextConfig

	val String.text: T get() = this.toText()

	fun T.displayText(hoverText: T? = null, command: String? = null): T {
		hoverText?.let { this.withHover(SHOW_TEXT, hoverText) }
		command?.let { this.withClick(SUGGEST_COMMAND, command) }
		return this
	}

	override fun PlayerCurrentInfo.nameText(origin: String): T {
		val text = origin.replace("playerName", this.name).text
		val hoverText = "".text
		textConfig.playerInfoHover.placeholderHandler(this).forEach { hoverText.addSibling(it.text) }
		val command = textConfig.playerInfoClickCommand.replace(this.placeholder)
		return text.displayText(hoverText, command)
	}

	override fun PlayerCurrentInfo.displayNameText(origin: String): T {
		val text = origin.replace("playerDisplayName", this.displayName).text
		val hoverText = "".text
		textConfig.playerInfoHover.placeholderHandler(this).forEach { hoverText.addSibling(it.text) }
		val command = textConfig.playerInfoClickCommand.replace(this.placeholder)
		return text.displayText(hoverText, command)
	}

	override fun Town.nameText(origin: String): T {
		if (this == Town.NONE) return "".text
		val text = origin.replace("townName", this.name).text
		val hoverText = "".text
		textConfig.townHover.placeholderHandler(this).forEach { hoverText.addSibling(it.text) }
		val command = textConfig.townClickCommand.replace(this.placeholder)
		return text.displayText(hoverText, command)
	}

	override fun Town.shortNameText(origin: String): T {
		if (this == Town.NONE) return "".text
		val text = origin.replace("townShortName", this.shortName).text
		val hoverText = "".text
		textConfig.townHover.placeholderHandler(this).forEach { hoverText.addSibling(it.text) }
		val command = textConfig.townClickCommand.replace(this.placeholder)
		return text.displayText(hoverText, command)
	}

	override fun ServerInfo.nameText(origin: String): T {
		val text = origin.replace("serverName", this.serverName).text
		val hoverText = "".text
		this.description.placeholderHandler(this).forEach { hoverText.addSibling(it.text) }
		val command = textConfig.serverInfoClickCommand.replace(this.placeholder)
		return text.displayText(hoverText, command)
	}

	override fun ServerInfo.idText(origin: String): T {
		val text = origin.replace("serverID", this.serverID).text
		val hoverText = "".text
		this.description.placeholderHandler(this).forEach { hoverText.addSibling(it.text) }
		val command = textConfig.serverInfoClickCommand.replace(this.placeholder)
		return text.displayText(hoverText, command)
	}

	val PlayerCurrentInfo.placeholder: Map<String, String>
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

	fun List<String>.placeholderHandler(playerInfo: PlayerCurrentInfo): List<String> = buildList {
		this@placeholderHandler.forEach { add(it.replace(playerInfo.placeholder)) }
	}

	val Town.placeholder: Map<String, String>
		get() {
			return buildMap {
				this["%id%"] = this@placeholder.id.toString()
				this["%name%"] = this@placeholder.name
				this["%shortName%"] = this@placeholder.shortName
				this["%createTime%"] = this@placeholder.formatTime()
				this["%mayor%"] = if (mayor.isNotEmpty()) this@placeholder.mayor.first.name else ""
				this["%admin%"] = this@placeholder.admin.map { it.name }.format(5, prefix = "[", suffix = "]")
				this["%member%"] = this@placeholder.member.map { it.name }.format(5, prefix = "[", suffix = "]")
			}
		}

	fun List<String>.placeholderHandler(town: Town): List<String> = buildList {
		this@placeholderHandler.forEach { add(it.replace(town.placeholder)) }
	}

	val ServerInfo.placeholder: Map<String, String>
		get() {
			return buildMap {
				this["%serverID%"] = serverID
				this["%serverName%"] = serverName
			}
		}

	fun List<String>.placeholderHandler(serverInfo: ServerInfo): List<String> = buildList {
		this@placeholderHandler.forEach { add(it.replace(serverInfo.placeholder)) }
	}
}