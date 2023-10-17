package cn.bakamc.folia

import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.db.initDataBase
import cn.bakamc.folia.event.registerEvent
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

object BakaMCPlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("BakaMCPlugin loading...")

        Configs.init(dataFolder.toPath())
        initDataBase()
        FlightEnergyManager.init()

        registerEvent()

        logger.info("BakaMCPlugin is enabled")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return super.onCommand(sender, command, label, args)
    }

    override fun onDisable() {
        FlightEnergyManager.onDisable()
    }

}