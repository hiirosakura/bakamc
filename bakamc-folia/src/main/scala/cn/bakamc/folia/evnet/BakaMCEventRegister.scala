package cn.bakamc.folia.evnet

import cn.bakamc.folia.api.Reloadable
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

private val events: List[Listener] = List(
  PlayerEventListener


)

def registerEvents(using plugin: JavaPlugin): Unit = {
  val manager = plugin.getServer.getPluginManager
  events.foreach(manager.registerEvents(_, plugin))

}

def reloadEvents(): Unit = events.foreach { case r: Reloadable => r.reload() }