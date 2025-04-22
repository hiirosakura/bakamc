package cn.bakamc.folia

import cn.bakamc.common.Bakamc
import cn.bakamc.folia.command.registerBakamcCommand
import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.db.initDataBase
import cn.bakamc.folia.event.onReload
import cn.bakamc.folia.event.registerEvent
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.hook.BakaMCHooks
import cn.bakamc.folia.item.SpecialItemManager
import cn.bakamc.folia.messagechannel.MessageChannels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BakaMCPlugin : JavaPlugin(), Bakamc {

    companion object {
        lateinit var instance: BakaMCPlugin
            private set

        internal val PluginDefaultScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

        internal val PluginIOScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    }

    override val bakaName: String
        get() = BuildConstants.NAME

    override val bakaVersion: String
        get() = BuildConstants.VERSION

    override val log: Logger = LoggerFactory.getLogger(bakaName)

    override fun onEnable() {
        instance = this
        log.info("BakaMCPlugin loading...")

        BakaMCHooks.onEnable(this)

        Configs.onLoaded {
            onReload()
            initDataBase()

            SpecialItemManager.init()
            FlightEnergyManager.init()
        }

        runBlocking {
            Configs.step(dataFolder.toPath())
        }

        MessageChannels.register(server)

        registerBakamcCommand()

        registerEvent()

        log.info("BakaMCPlugin is enabled")
    }

    fun reload() {

        server.asyncScheduler.cancelTasks(this)
        FlightEnergyManager.onDisable()
        SpecialItemManager.onDisable()

        runBlocking {
            Configs.load()
        }

    }

    override fun onDisable() {
        BakaMCHooks.onDisable(this)
        server.asyncScheduler.cancelTasks(this)
        FlightEnergyManager.onDisable()
        SpecialItemManager.onDisable()
        PluginDefaultScope.cancel()
        PluginIOScope.cancel()
    }


}