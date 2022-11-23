package cn.bakamc.spigot.common

import cn.bakamc.common.common.AbstractPlatform
import cn.bakamc.common.common.PlayerCurrentInfo
import cn.bakamc.common.common.PlayerInfo
import cn.bakamc.common.town.Town
import cn.bakamc.common.utils.f
import cn.bakamc.spigot.config.SpigotCommonConfig
import cn.bakamc.spigot.town.SpigotTownManager
import net.minecraft.network.chat.ChatClickable
import net.minecraft.network.chat.ChatClickable.EnumClickAction
import net.minecraft.network.chat.ChatComponentText
import net.minecraft.network.chat.ChatHoverable
import net.minecraft.network.chat.ChatHoverable.EnumHoverAction
import net.minecraft.network.chat.IChatBaseComponent.ChatSerializer
import net.minecraft.network.chat.IChatMutableComponent
import org.bukkit.Server
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.common

 * 文件名 SpigotPlatform

 * 创建时间 2022/11/22 11:24

 * @author forpleuvoir

 */
object SpigotPlatform : AbstractPlatform<IChatMutableComponent, Player, Server>(SpigotCommonConfig.INSTANCE) {
	override fun displayText(display: IChatMutableComponent, hoverText: IChatMutableComponent?, command: String?): IChatMutableComponent {
		display.a {
			var style = it
			hoverText?.let { style = style.a(ChatHoverable((EnumHoverAction.a), hoverText)) }
			command?.let { style = style.a(ChatClickable(EnumClickAction.d, command)) }
			style
		}
		return display
	}

	override fun addSiblings(origin: IChatMutableComponent,sibling: IChatMutableComponent): IChatMutableComponent {
		return origin.a(sibling)
	}

	override fun textToJson(text: IChatMutableComponent): String {
		return ChatSerializer.a(text)
	}

	override fun textToPlainString(text: IChatMutableComponent): String {
		return text.string
	}

	override fun textFromJson(json: String): IChatMutableComponent {
		return ChatSerializer.a(json)!!
	}

	override fun stringToText(str: String): IChatMutableComponent {
		return ChatComponentText(str)
	}

	override fun players(server: Server): Iterable<Player> {
		return server.onlinePlayers
	}

	override fun playerInfo(player: Player): PlayerInfo {
		return PlayerInfo(
			player.uniqueId,
			player.name,
			player.displayName
		)
	}

	override fun playerCurrentInfo(player: Player): PlayerCurrentInfo {
		var town: Town = Town.NONE
		SpigotTownManager.hasManager { town = it.getByPlayerID(player.uniqueId) }
		return PlayerCurrentInfo(
			player.uniqueId,
			player.name,
			player.displayName,
			town,
			player.level,
			player.totalExperience,
			player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value?.f ?: 20f,
			player.health.f,
			player.world.key.toString(),
		)
	}
}