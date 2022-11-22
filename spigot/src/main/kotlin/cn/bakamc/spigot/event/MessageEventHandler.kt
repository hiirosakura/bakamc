package cn.bakamc.spigot.event

import cn.bakamc.spigot.chat.SpigotMessageHandler
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.logging.Level

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.spigot.event

 * 文件名 MessageEventHandler

 * 创建时间 2022/11/22 21:28

 * @author forpleuvoir

 */
object MessageEventHandler : Listener {

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

}