package cn.bakamc.folia

import cn.bakamc.folia.command.registerCommand
import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.event.registerEvent
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.item.SpecialItemManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class BakaMCPlugin : JavaPlugin() {

    companion object{
        lateinit var instance: BakaMCPlugin
            private set
    }


    override fun onEnable() {
        instance = this
        logger.info("BakaMCPlugin loading...")

        Configs.init(dataFolder.toPath())
        registerCommand()
        SpecialItemManager.init()
        FlightEnergyManager.init()

        registerEvent()

        logger.info("BakaMCPlugin is enabled")
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>?): MutableList<String>? {
        return super.onTabComplete(sender, command, alias, args)
    }

    override fun onDisable() {
        server.asyncScheduler.cancelTasks(this)
        FlightEnergyManager.onDisable()
        SpecialItemManager.onDisable()
    }

}