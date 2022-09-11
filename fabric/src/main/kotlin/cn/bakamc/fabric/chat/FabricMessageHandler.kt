package cn.bakamc.fabric.chat

import cn.bakamc.common.chat.AbstractMessageHandler
import cn.bakamc.common.chat.config.ChatConfig
import cn.bakamc.common.common.PlayerCurrentInfo
import cn.bakamc.common.common.ServerInfo
import cn.bakamc.common.town.Town
import cn.bakamc.common.utils.replace
import cn.bakamc.fabric.config.Config
import net.minecraft.item.ItemStack
import net.minecraft.network.MessageType.CHAT
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.*
import net.minecraft.text.ClickEvent.Action.SUGGEST_COMMAND
import net.minecraft.text.HoverEvent.Action
import net.minecraft.text.HoverEvent.ItemStackContent
import net.minecraft.util.Formatting.ITALIC
import java.util.*
import java.util.function.Consumer

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.chat

 * 文件名 FabricMessageHandler

 * 创建时间 2022/9/6 13:29

 * @author forpleuvoir

 */
class FabricMessageHandler(config: ChatConfig, private val server: MinecraftServer) :
	AbstractMessageHandler<MutableText, ServerPlayerEntity>(config) {

	companion object {
		@JvmStatic
		lateinit var INSTANCE: FabricMessageHandler
			private set

		@JvmStatic
		fun hasHandler(action: Consumer<FabricMessageHandler>) {
			if (this::INSTANCE.isInitialized) {
				action.accept(INSTANCE)
			}
		}

		@JvmStatic
		fun init(server: MinecraftServer): FabricMessageHandler {
			INSTANCE = FabricMessageHandler(Config.Chat, server)
			INSTANCE.connect()
			return INSTANCE
		}
	}

	override fun ServerPlayerEntity.getItemText(index: Int): MutableText {
		val item = when (index) {
			-2   -> this.offHandStack
			-1   -> this.mainHandStack
			else -> this.inventory.getStack(index)
		}
		return item.getHoverableText()
	}

	private fun ItemStack.getHoverableText(): MutableText {
		val mutableText: MutableText = LiteralText("").append(this.name)
		val count = this.count
		if (count > 1) mutableText.append(" x$count")
		if (this.hasCustomName()) {
			mutableText.formatted(ITALIC)
		}
		val mutableText2 = Texts.bracketed(mutableText)
		if (!this.isEmpty) {
			mutableText2.formatted(this.rarity.formatting).styled { style: Style ->
				style.withHoverEvent(
					HoverEvent(Action.SHOW_ITEM, ItemStackContent(this))
				)
			}
		}
		return mutableText2
	}

	override fun ServerPlayerEntity.sendMessage(message: MutableText, uuid: UUID) {
		this.sendMessage(message, CHAT, uuid)
	}

	override fun MutableText.addSiblings(text: MutableText): MutableText = this.append(text)


	override fun MutableText.toJson(): String = Text.Serializer.toJson(this)


	override fun String.fromJson(): MutableText = Text.Serializer.fromJson(this)!!


	override val String.text: MutableText
		get() = LiteralText(this)

	override fun PlayerCurrentInfo.text(origin: String): MutableText {
		val text = origin.replace("playerName", this.name).text
		val hoverText = "".text
		riguruChatConfig.playerInfoHover.placeholderHandler(this).forEach { hoverText.append(it.text) }
		val command = riguruChatConfig.playerInfoClickCommand.replace(this.placeholder)
		return displayText(text, hoverText, command)
	}

	override fun ServerInfo.text(origin: String): MutableText {
		val text = origin.replace("serverName", this.serverName).text
		val hoverText = "".text
		this.description.placeholderHandler(this).forEach { hoverText.append(it.text) }
		val command = riguruChatConfig.serverInfoClickCommand.replace(this.placeholder)
		return displayText(text, hoverText, command)
	}

	override fun ServerInfo.idText(origin: String): MutableText {
		val text = origin.replace("serverID", this.serverID).text
		val hoverText = "".text
		this.description.placeholderHandler(this).forEach { hoverText.append(it.text) }
		val command = riguruChatConfig.serverInfoClickCommand.replace(this.placeholder)
		return displayText(text, hoverText, command)
	}

	override fun PlayerCurrentInfo.displayNameText(origin: String): MutableText {
		val text = origin.replace("playerDisplayName", this.displayName).text
		val hoverText = "".text
		riguruChatConfig.playerInfoHover.placeholderHandler(this).forEach { hoverText.append(it.text) }
		val command = riguruChatConfig.playerInfoClickCommand.replace(this.placeholder)
		return displayText(text, hoverText, command)
	}

	override fun PlayerCurrentInfo.townNameText(origin: String): MutableText {
		val town = town.name
		return if (town.isNotEmpty()) origin.replace("townName", town).text else "".text
	}

	override fun PlayerCurrentInfo.townShortNameText(origin: String): MutableText {
		val town = town.shortName
		return if (town.isNotEmpty()) origin.replace("townShortName", town).text else "".text
	}


	private fun displayText(display: MutableText, hoverText: MutableText, command: String): MutableText {
		display.styled {
			var style = it
			style = style.withHoverEvent(HoverEvent((Action.SHOW_TEXT), hoverText))
			style = style.withClickEvent(ClickEvent(SUGGEST_COMMAND, command))
			style
		}
		return display
	}

	override val players: Iterable<ServerPlayerEntity>
		get() = server.playerManager.playerList

	override val ServerPlayerEntity.info: PlayerCurrentInfo
		get() {
			return PlayerCurrentInfo(
				uuid,
				displayName.string,
				name.string,
				Town.NONE,
				experienceLevel,
				totalExperience,
				maxHealth,
				health,
				world.registryKey.value.toString(),
			)
		}
}