package cn.bakamc.fabric.common

import cn.bakamc.common.common.AbstractPlatform
import cn.bakamc.common.common.PlayerCurrentInfo
import cn.bakamc.common.common.PlayerInfo
import cn.bakamc.common.town.Town
import cn.bakamc.fabric.config.FabricCommonConfig
import cn.bakamc.fabric.town.FabricTownManager
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.*
import net.minecraft.text.ClickEvent.Action.SUGGEST_COMMAND
import net.minecraft.text.HoverEvent.Action

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
		display.styled {
			var style = it
			hoverText?.let { style = style.withHoverEvent(HoverEvent((Action.SHOW_TEXT), hoverText)) }
			command?.let { style = style.withClickEvent(ClickEvent(SUGGEST_COMMAND, command)) }
			style
		}
		return display
	}

	override fun addSiblings(origin: MutableText,  sibling: MutableText): MutableText {
		return origin.append(sibling)
	}

	override fun textToJson(text: MutableText): String = Text.Serializer.toJson(text)

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