package cn.bakamc.folia.event

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.event.entity.EntityChangedBlockEventListener
import cn.bakamc.folia.event.entity.PlayerEventListener
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin


private val registrationList = listOf(
    EntityChangedBlockEventListener,
    FlightEnergyManager,
    PlayerEventListener

)

fun JavaPlugin.registerEvent() {
    server.pluginManager.run {
        registrationList.forEach { register(it) }
    }
}

private fun PluginManager.register(listener: Listener) {
    this.registerEvents(listener, BakaMCPlugin.instance)
}
