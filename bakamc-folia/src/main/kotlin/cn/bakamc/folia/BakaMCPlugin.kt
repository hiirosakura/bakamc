package cn.bakamc.folia

import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.event.registerEvent
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import org.bukkit.plugin.java.JavaPlugin

object BakaMCPlugin: JavaPlugin() {

    override fun onEnable() {
        logger.info("BakaMCPlugin loading...")

        Configs.init(dataFolder.toPath())
        cn.bakamc.folia.db.init()
        FlightEnergyManager.init()

        registerEvent()

        logger.info("BakaMCPlugin is enabled")
    }


    override fun onDisable() {
        FlightEnergyManager.onDisable()
    }

}