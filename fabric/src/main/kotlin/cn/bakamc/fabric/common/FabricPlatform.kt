package cn.bakamc.fabric.common

import cn.bakamc.common.common.AbstractPlatform
import cn.bakamc.common.common.MultiPlatform.ClickAction
import cn.bakamc.common.common.MultiPlatform.ClickAction.*
import cn.bakamc.common.common.MultiPlatform.HoverAction
import cn.bakamc.common.common.MultiPlatform.HoverAction.*
import cn.bakamc.common.player.PlayerCurrentInfo
import cn.bakamc.common.player.PlayerInfo
import cn.bakamc.common.town.Town
import cn.bakamc.fabric.town.FabricTownHandler
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.HoverEvent.EntityContent
import net.minecraft.text.HoverEvent.ItemStackContent
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import java.util.*
import net.minecraft.text.ClickEvent.Action as Click
import net.minecraft.text.HoverEvent.Action as Hover

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.fabric.common

 * 文件名 FabricPlatform

 * 创建时间 2022/11/16 21:46

 * @author forpleuvoir

 */
interface FabricPlatform : AbstractPlatform<MutableText, ServerPlayerEntity, MinecraftServer> {

	override fun MutableText.addSibling(sibling: MutableText): MutableText {
		return this.append(sibling)
	}

	override fun MutableText.withClick(action: ClickAction, content: String): MutableText {
		val a = when (action) {
			OPEN_URL          -> Click.OPEN_URL
			OPEN_FILE         -> Click.OPEN_FILE
			RUN_COMMAND       -> Click.RUN_COMMAND
			SUGGEST_COMMAND   -> Click.SUGGEST_COMMAND
			CHANGE_PAGE       -> Click.CHANGE_PAGE
			COPY_TO_CLIPBOARD -> Click.COPY_TO_CLIPBOARD
		}
		return this.styled {
			it.withClickEvent(ClickEvent(a, content))
		}
	}

	override fun MutableText.withHover(action: HoverAction, content: Any): MutableText {
		val event = when (action) {
			SHOW_TEXT   -> HoverEvent(Hover.SHOW_TEXT, content as Text)
			SHOW_ITEM   -> HoverEvent(Hover.SHOW_ITEM, content as ItemStackContent)
			SHOW_ENTITY -> HoverEvent(Hover.SHOW_ENTITY, content as EntityContent)
		}
		return this.styled {
			it.withHoverEvent(event)
		}
	}

	override fun MutableText.toJson(): String = Text.Serializer.toJson(this)

	override fun MutableText.toPlain(): String = this.string

	override fun String.fromJson(): MutableText = Text.Serializer.fromJson(this)!!

	override fun String.toText(): MutableText = Text.literal(this)

	override fun MinecraftServer.players(): Iterable<ServerPlayerEntity> = this.playerManager.playerList

	override fun ServerPlayerEntity.sendMessage(message: MutableText, uuid: UUID) {
		this.sendMessage(message)
	}

	override fun ServerPlayerEntity.playerInfo(): PlayerInfo {
		return PlayerInfo(
			this.uuid,
			this.displayName.string,
			this.name.string
		)
	}

	override fun ServerPlayerEntity.playerCurrentInfo(): PlayerCurrentInfo {
		var town: Town = Town.NONE
		FabricTownHandler.hasHandler { town = it.getByPlayerID(this.uuid) }
		return PlayerCurrentInfo(
			this.uuid,
			this.name.string,
			this.displayName.string,
			town,
			this.experienceLevel,
			this.totalExperience,
			this.maxHealth,
			this.health,
			this.world.registryKey.value.toString(),
		)
	}

}