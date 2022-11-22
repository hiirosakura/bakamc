package cn.bakamc.spigot.common

import cn.bakamc.common.common.AbstractPlatform
import cn.bakamc.common.common.PlayerCurrentInfo
import cn.bakamc.common.common.PlayerInfo
import cn.bakamc.common.town.Town
import cn.bakamc.common.utils.f
import cn.bakamc.spigot.config.SpigotCommonConfig
import net.minecraft.network.chat.ChatComponentText
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
		TODO("Not yet implemented")
	}

	override fun addSiblings(origin: IChatMutableComponent, vararg sibling: IChatMutableComponent): IChatMutableComponent {
		for (mutableText in sibling) {
			origin.a(mutableText)
		}
		return origin
	}

	override fun textToJson(text: IChatMutableComponent): String {
		return ChatSerializer.a(text)
	}

	override fun textFromJson(json: String): IChatMutableComponent {
		return ChatSerializer.a(json)!!
	}

	override fun stringToText(str: String): IChatMutableComponent {
		return ChatComponentText("")
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
		return PlayerCurrentInfo(
			player.uniqueId,
			player.name,
			player.displayName,
			Town.NONE,
			player.level,
			player.totalExperience,
			player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value?.f ?: 20f,
			player.health.f,
			player.world.key.toString(),
		)
	}
}