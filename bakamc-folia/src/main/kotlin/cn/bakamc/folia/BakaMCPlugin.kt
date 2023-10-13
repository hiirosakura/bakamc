package cn.bakamc.folia

import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.event.registerEvent
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import org.bukkit.plugin.java.JavaPlugin

object BakaMCPlugin: JavaPlugin() {
    override fun onEnable() {
        Configs.init(dataFolder.toPath())
        FlightEnergyManager.init()

        registerEvent()
    }


    override fun onDisable() {
    }

}