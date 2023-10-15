package cn.bakamc.folia.flight_energy

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.config.Configs
import cn.bakamc.folia.extension.onlinePlayers
import cn.bakamc.folia.service.PlayerService
import cn.bakamc.folia.util.SimpleTimerTask
import cn.bakamc.folia.util.schedule
import moe.forpleuvoir.nebula.common.api.Initializable
import moe.forpleuvoir.nebula.common.util.minute
import moe.forpleuvoir.nebula.common.util.second
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*
import java.util.concurrent.ConcurrentHashMap


object FlightEnergyManager : Listener, Initializable {

    private lateinit var timer: Timer

    private lateinit var energyCache: MutableMap<Player, Double>

    private lateinit var tasks: List<SimpleTimerTask>

    override fun init() {
        tasks = listOf(
            //tick
            SimpleTimerTask(0L, 1.second, ::tick),
            //sync
            SimpleTimerTask(1.minute, Configs.FlightEnergy.SYNC_PERIOD.toLong(), ::sync)
        )

        timer = Timer("[${BakaMCPlugin.name}]FlightEnergyManager").apply {
            tasks.forEach { this.schedule(it) }
        }

        energyCache = ConcurrentHashMap()
        energyCache.putAll(PlayerService.getFlightEnergy(onlinePlayers))

    }

    fun onDisable() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        if (this::energyCache.isInitialized) {
            sync()
        }
        if (this::energyCache.isInitialized) {
            energyCache.clear()
        }
    }

    fun onPlayerJoin(player: Player) {
        energyCache[player] = PlayerService.getFlightEnergy(player)
    }


    fun sync() {
        PlayerService.updateFlightEnergy(energyCache)
    }

    /**
     * 玩家当前的飞行能量
     */
    val Player.energy: Double
        get() {
            return energyCache[this] ?: PlayerService.getFlightEnergy(this)
        }


    /**
     * 由单独线程控制循环
     * 每秒执行一次
     */
    private fun tick() {


    }


}