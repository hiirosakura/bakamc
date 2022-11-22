package cn.bakamc.spigot.chat

import cn.bakamc.common.chat.AbstractMessageHandler
import cn.bakamc.common.config.common.CommonConfig
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.spigot.common.SpigotPlatform
import net.minecraft.EnumChatFormat
import net.minecraft.network.chat.ChatComponentText
import net.minecraft.network.chat.ChatComponentUtils
import net.minecraft.network.chat.ChatMessageType
import net.minecraft.network.chat.IChatMutableComponent
import net.minecraft.network.protocol.game.PacketPlayOutChat
import net.minecraft.world.item.ItemStack
import org.bukkit.Server
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack
import org.bukkit.entity.Player
import java.util.*

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.chat

 * 文件名 SpigotMessageHandler

 * 创建时间 2022/11/22 11:16

 * @author forpleuvoir

 */
class SpigotMessageHandler(config: ServerConfig, override val commonConfig: CommonConfig, override val server: Server) :
	AbstractMessageHandler<IChatMutableComponent, Player, Server>(config, SpigotPlatform, commonConfig) {

	override fun Player.getItemText(index: Int): IChatMutableComponent {
		val item = when (index) {
			-2   -> this.inventory.itemInOffHand
			-1   -> this.inventory.itemInMainHand
			else -> this.inventory.getItem(index)
		}
		if (item?.type?.isAir == true) {
			return if (index == -2) "%i".text else if (index == -1) "%o".text else "%$index".text
		}
		return CraftItemStack.asNMSCopy(item!!).getHoverableText()
	}

	private fun ItemStack.getHoverableText(): IChatMutableComponent {
		val mutableText: IChatMutableComponent = ChatComponentText("").a(this.w())
		val count = this.J()
		if (count > 1) mutableText.a(ChatComponentText(" x$count"))
		if (this.y()) {
			mutableText.a(EnumChatFormat.o)
		}
		val mutableText2 = ChatComponentUtils.a(mutableText)
		if (!this.s()) {
			mutableText2.a(this.A().e).a { style: Style ->
				style.withHoverEvent(
					HoverEvent(Action.SHOW_ITEM, ItemStackContent(this))
				)
			}
		}
		return mutableText2
	}

	override fun Player.sendMessage(message: IChatMutableComponent, uuid: UUID) {
		(this as CraftPlayer).apply {
			if (this.handle.b != null) {
				val packet = PacketPlayOutChat(message, ChatMessageType.a, uuid)
				this.handle.b.a(packet)
			}
		}
	}
}