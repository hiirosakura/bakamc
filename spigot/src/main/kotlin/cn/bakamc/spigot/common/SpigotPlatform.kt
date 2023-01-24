package cn.bakamc.spigot.common

import cn.bakamc.common.common.AbstractPlatform
import cn.bakamc.common.common.MultiPlatform.ClickAction
import cn.bakamc.common.common.MultiPlatform.ClickAction.*
import cn.bakamc.common.common.MultiPlatform.HoverAction
import cn.bakamc.common.common.MultiPlatform.HoverAction.*
import cn.bakamc.common.player.PlayerCurrentInfo
import cn.bakamc.common.player.PlayerInfo
import cn.bakamc.common.town.Town
import cn.bakamc.common.utils.f
import cn.bakamc.spigot.town.SpigotTownHandler
import net.minecraft.network.chat.*
import net.minecraft.network.chat.ChatClickable.EnumClickAction
import net.minecraft.network.chat.ChatHoverable.EnumHoverAction
import net.minecraft.network.chat.ChatMessageType.a
import net.minecraft.network.chat.IChatBaseComponent.ChatSerializer
import net.minecraft.network.protocol.game.PacketPlayOutChat
import org.bukkit.Server
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.common

 * 文件名 SpigotPlatform

 * 创建时间 2022/11/22 11:24

 * @author forpleuvoir

 */
interface SpigotPlatform : AbstractPlatform<IChatMutableComponent, Player, Server> {
	override fun IChatMutableComponent.displayText(hoverText: IChatMutableComponent?, command: String?): IChatMutableComponent {
		this.a {
			var style = it
			hoverText?.let { style = style.a(ChatHoverable((EnumHoverAction.a), hoverText)) }
			command?.let { style = style.a(ChatClickable(EnumClickAction.d, command)) }
			style
		}
		return this
	}

	override fun IChatMutableComponent.addSibling(sibling: IChatMutableComponent): IChatMutableComponent {
		return this.a(sibling)
	}

	override fun IChatMutableComponent.withClick(action: ClickAction, content: String): IChatMutableComponent {
		val a = when (action) {
			OPEN_URL          -> EnumClickAction.a
			OPEN_FILE         -> EnumClickAction.b
			RUN_COMMAND       -> EnumClickAction.c
			SUGGEST_COMMAND   -> EnumClickAction.d
			CHANGE_PAGE       -> EnumClickAction.e
			COPY_TO_CLIPBOARD -> EnumClickAction.f
		}
		return this.a {
			it.a(ChatClickable(a, content))
		}
	}

	override fun IChatMutableComponent.withHover(action: HoverAction, content: Any): IChatMutableComponent {
		val event = when (action) {
			SHOW_TEXT   -> ChatHoverable(EnumHoverAction.a, content as IChatBaseComponent)
			SHOW_ITEM   -> ChatHoverable(EnumHoverAction.b, content as ChatHoverable.c)
			SHOW_ENTITY -> ChatHoverable(EnumHoverAction.c, content as ChatHoverable.b)
		}
		return this.a {
			it.a(event)
		}
	}

	override fun IChatMutableComponent.toJson(): String = ChatSerializer.a(this)

	override fun IChatMutableComponent.toPlain(): String = this.string

	override fun String.fromJson(): IChatMutableComponent = ChatSerializer.a(this)!!

	override fun String.toText(): IChatMutableComponent = ChatComponentText(this)

	override fun Server.players(): Iterable<Player> {
		return this.onlinePlayers
	}

	override fun Player.sendMessage(message: IChatMutableComponent, uuid: UUID) {
		(this as CraftPlayer).apply {
			if (this.handle.b != null) {
				val packet = PacketPlayOutChat(message, a, uuid)
				this.handle.b.a(packet)
			}
		}
	}

	override fun Player.playerInfo(): PlayerInfo {
		return PlayerInfo(
			this.uniqueId,
			this.name,
			this.displayName
		)
	}

	override fun Player.playerCurrentInfo(): PlayerCurrentInfo {
		var town: Town = Town.NONE
		SpigotTownHandler.hasHandler { town = it.getByPlayerID(this.uniqueId) }
		return PlayerCurrentInfo(
			this.uniqueId,
			this.name,
			this.displayName,
			town,
			this.level,
			this.totalExperience,
			this.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value?.f ?: 20f,
			this.health.f,
			this.world.key.toString(),
		)
	}
}