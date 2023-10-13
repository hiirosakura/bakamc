package cn.bakamc.folia.event

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.event.entity.EnderManEventListener
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager


private val registrationList = listOf(
    EnderManEventListener,
    FlightEnergyManager

)

fun registerEvent() {
    BakaMCPlugin.server.pluginManager.run {
        registrationList.forEach { register(it) }
    }
}

private fun PluginManager.register(listener: Listener){
    this.registerEvents(listener,BakaMCPlugin)
}