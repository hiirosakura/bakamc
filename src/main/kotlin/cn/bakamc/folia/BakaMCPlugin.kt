package cn.bakamc.folia

import cn.bakamc.folia.command.registerCommand
import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.db.initDataBase
import cn.bakamc.folia.event.entity.EntityChangedBlockEventListener
import cn.bakamc.folia.event.registerEvent
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.item.SpecialItemManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin

class BakaMCPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: BakaMCPlugin
            private set

        internal lateinit var PluginScope: CoroutineScope
            private set
    }


    override fun onEnable() {
        instance = this
        PluginScope = CoroutineScope(Dispatchers.IO)
        logger.info("BakaMCPlugin loading...")

        Configs.onLoaded {
            EntityChangedBlockEventListener.reloadCache()

            initDataBase()

            SpecialItemManager.init()
            FlightEnergyManager.init()
        }

        runBlocking {
            Configs.init(dataFolder.toPath())
        }

        registerCommand()

        registerEvent()

        logger.info("BakaMCPlugin is enabled")
    }

    fun reload(){
        server.asyncScheduler.cancelTasks(this)
        FlightEnergyManager.onDisable()
        SpecialItemManager.onDisable()

        runBlocking {
            Configs.load()
        }

    }

    override fun onDisable() {
        server.asyncScheduler.cancelTasks(this)
        FlightEnergyManager.onDisable()
        SpecialItemManager.onDisable()
        PluginScope.cancel()
    }

}