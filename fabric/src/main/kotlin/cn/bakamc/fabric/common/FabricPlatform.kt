package cn.bakamc.fabric.common

import cn.bakamc.common.common.AbstractPlatform
import cn.bakamc.common.common.MultiPlatform.ClickAction
import cn.bakamc.common.common.MultiPlatform.ClickAction.SUGGEST_COMMAND
import cn.bakamc.common.common.MultiPlatform.HoverAction
import cn.bakamc.common.common.MultiPlatform.HoverAction.*
import cn.bakamc.common.common.PlayerCurrentInfo
import cn.bakamc.common.common.PlayerInfo
import cn.bakamc.common.town.Town
import cn.bakamc.fabric.config.FabricCommonConfig
import cn.bakamc.fabric.town.FabricTownManager
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.*
import net.minecraft.text.ClickEvent.Action as Click
import net.minecraft.text.HoverEvent.Action as Hover
import net.minecraft.text.HoverEvent.EntityContent
import net.minecraft.text.HoverEvent.ItemStackContent

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.common

 * 文件名 FabricPlatform

 * 创建时间 2022/11/16 21:46

 * @author forpleuvoir

 */
object FabricPlatform : AbstractPlatform<MutableText, ServerPlayerEntity, MinecraftServer>(FabricCommonConfig.INSTANCE) {

	override fun displayText(display: MutableText, hoverText: MutableText?, command: String?): MutableText {
		hoverText?.let { withHover(display, SHOW_TEXT, hoverText) }
		command?.let { withClick(display, SUGGEST_COMMAND, command) }
		return display
	}

	override fun addSiblings(origin: MutableText, sibling: MutableText): MutableText {
		return origin.append(sibling)
	}

	override fun withClick(text: MutableText, action: ClickAction, value: String): MutableText {
		return text.styled {
			it.withClickEvent(ClickEvent(Click.valueOf(action.name), value))
		}
	}

	override fun withHover(text: MutableText, action: HoverAction, content: Any): MutableText {
		val event = when (action) {
			SHOW_TEXT   -> HoverEvent(Hover.SHOW_TEXT, content as MutableText)
			SHOW_ITEM   -> HoverEvent(Hover.SHOW_ITEM, content as ItemStackContent)
			SHOW_ENTITY -> HoverEvent(Hover.SHOW_ENTITY, content as EntityContent)
		}
		return text.styled {
			it.withHoverEvent(event)
		}
	}


	override fun textToJson(text: MutableText): String = Text.Serializer.toJson(text)
	override fun textToPlainString(text: MutableText): String = text.string

	override fun textFromJson(json: String): MutableText = Text.Serializer.fromJson(json)!!

	override fun stringToText(str: String): MutableText = LiteralText(str)

	override fun players(server: MinecraftServer): Iterable<ServerPlayerEntity> = server.playerManager.playerList

	override fun playerInfo(player: ServerPlayerEntity): PlayerInfo {
		return PlayerInfo(
			player.uuid,
			player.displayName.string,
			player.name.string
		)
	}

	override fun playerCurrentInfo(player: ServerPlayerEntity): PlayerCurrentInfo {
		var town: Town = Town.NONE
		FabricTownManager.hasManager { town = it.getByPlayerID(player.uuid) }
		return PlayerCurrentInfo(
			player.uuid,
			player.name.string,
			player.displayName.string,
			town,
			player.experienceLevel,
			player.totalExperience,
			player.maxHealth,
			player.health,
			player.world.registryKey.value.toString(),
		)
	}

}