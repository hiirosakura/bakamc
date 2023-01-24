package cn.bakamc.spigot.event

import cn.bakamc.spigot.chat.SpigotMessageHandler
import cn.bakamc.spigot.player.SpigotPlayerHandler
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.logging.Level

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.event

 * 文件名 MessageEventHandler

 * 创建时间 2022/11/22 21:28

 * @author forpleuvoir

 */
object PlayerEventHandler : Listener {

	private val log = Bukkit.getLogger()

	@EventHandler
	fun asyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
		if (event.isCancelled) return
		SpigotMessageHandler.hasHandler {
			try {
				it.sendChatMessage(event.player, event.message)
				event.isCancelled = true
			} catch (e: Exception) {
				log.log(Level.SEVERE, "消息发送失败", e)
			}
		}
	}

	@EventHandler
	fun onPlayerJoin(event: PlayerJoinEvent) {
		SpigotPlayerHandler.hasHandler {
			it.onPlayerJoin(event.player)
		}
	}

	@EventHandler
	fun onPlayerLeft(event: PlayerQuitEvent) {
		SpigotPlayerHandler.hasHandler {
			it.onPlayerLeft(event.player)
		}
	}

}