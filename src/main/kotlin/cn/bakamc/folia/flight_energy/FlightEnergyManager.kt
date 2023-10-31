package cn.bakamc.folia.flight_energy

import cn.bakamc.folia.config.Configs.FlightEnergy.ENERGY_COST
import cn.bakamc.folia.config.Configs.FlightEnergy.MONEY_ITEM
import cn.bakamc.folia.config.Configs.FlightEnergy.SYNC_PERIOD
import cn.bakamc.folia.config.Configs.FlightEnergy.TICK_PERIOD
import cn.bakamc.folia.db.table.FlightEnergy
import cn.bakamc.folia.db.table.SpecialItem
import cn.bakamc.folia.extension.onlinePlayers
import cn.bakamc.folia.extension.uuid
import cn.bakamc.folia.item.SpecialItemManager
import cn.bakamc.folia.service.PlayerService
import cn.bakamc.folia.util.*
import kotlinx.coroutines.runBlocking
import moe.forpleuvoir.nebula.common.api.Initializable
import net.minecraft.server.level.ServerPlayer
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.jetbrains.annotations.Contract
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue


object FlightEnergyManager : Listener, Initializable {

    private lateinit var energyCache: MutableMap<Player, FlightEnergy>

    private lateinit var energyBar: MutableMap<Player, EnergyBar>

    private lateinit var tasks: List<AsyncTask>

    private var syncing = AtomicBoolean(false)

    /**
     * 玩家当前的飞行能量
     */
    var Player.energy: Double
        get() {
            return energyCache[this]?.energy ?: runBlocking { PlayerService.getFlightEnergy(this@energy).energy }
        }
        set(value) {
            energyCache[this]?.energy = value
        }

    var Player.barVisible: Boolean
        get() {
            return energyCache[this]?.barVisible ?: runBlocking { PlayerService.getFlightEnergy(this@barVisible).barVisible }
        }
        set(value) {
            energyCache[this]?.barVisible = value
        }

    var ServerPlayer.energy: Double
        get() {
            return energyCache.keys.find {
                it.uuid == this.stringUUID
            }?.energy ?: runBlocking {
                bakamc.server.getPlayer(this@energy.stringUUID)?.let {
                    PlayerService.getFlightEnergy(it).energy
                } ?: 0.0
            }

        }
        set(value) {
            energyCache.keys.find {
                it.uuid == this.stringUUID
            }?.let {
                energyCache[it]?.energy = value
            }
        }

    override fun init() {
        syncing.set(false)
        tasks = listOf(
            //tick
            AsyncTask(0.seconds, TICK_PERIOD) { tick() },
            //sync
            AsyncTask(1.minutes, SYNC_PERIOD) { sync() }
        )

        tasks.forEach { runAtFixedRate(it) }

        energyCache = ConcurrentHashMap()

        energyBar = ConcurrentHashMap()

        runBlocking {
            energyCache.putAll(PlayerService.getFlightEnergies(onlinePlayers))
            energyCache.forEach { (player, flightEnergy) ->
                energyBar[player] = EnergyBar.create(server, player, flightEnergy)
            }
            logger.info("飞行能量加载完成")
        }

    }

    fun onDisable() {
        if (this::energyCache.isInitialized) {
            if (!syncing.get()) sync()
            energyCache.clear()
            energyBar.clear()
        }
    }

    /**
     * 获取货币对应的飞行能量
     * @return Map<SpecialItem, Double>
     */
    fun moneyItem(): Map<SpecialItem, Double> {
        return buildMap {
            SpecialItemManager.specifyType(MONEY_ITEM.keys).forEach { (key, specialItem) ->
                this[specialItem] = MONEY_ITEM[key]!!
            }
        }
    }

    /**
     * 获取指定货币对应的飞行能量
     * @param key String
     * @return Pair<SpecialItem, Double>?
     */
    fun moneyItem(key: String): Pair<SpecialItem, Double>? {
        return SpecialItemManager.specifyType(MONEY_ITEM.keys)[key]?.let {
            it to MONEY_ITEM[key]!!
        }
    }

    suspend fun onPlayerJoin(player: Player) {
        energyCache[player] = PlayerService.getFlightEnergy(player)
        energyBar[player] = EnergyBar.create(server, player, energyCache[player]!!)
        if (player.gameMode == GameMode.SURVIVAL) {
            player.allowFlight = energyCache[player]!!.enabled
            player.isFlying = true
        }
    }

    suspend fun onPlayerQuit(player: Player) {
        PlayerService.updateFlightEnergy(energyCache[player]!!)
        energyCache.remove(player)
        energyBar.remove(player)?.close()
    }

    fun onPlayerRespawn(player: Player) {
        if (player.gameMode == GameMode.SURVIVAL)
            player.allowFlight = energyCache[player]!!.enabled
    }

    fun onPlayerGameModeChange(player: Player, newGameMode: GameMode) {
        if (newGameMode == GameMode.SURVIVAL) {
            val isFlying = player.isFlying
            runDelayed(0.1.seconds) {
                player.allowFlight = energyCache[player]!!.enabled
                player.isFlying = isFlying
            }
        } else {
            energyBar[player]!!.setVisible(false)
        }
    }

    /**
     * 切换飞行状态
     * @param player Player
     * @param enabled Boolean?
     */
    @Contract("_, null -> !enabled")
    fun toggleFly(player: Player, enabled: Boolean? = null) {
        energyCache[player]?.let {
            it.enabled = enabled ?: !it.enabled
            if (!it.enabled) energyBar[player]!!.setVisible(false)
            if (player.gameMode == GameMode.SURVIVAL)
                player.allowFlight = it.enabled
        }
    }


    private fun sync() {
        runBlocking {
            syncing.set(true)
            measureTimedValue {
                PlayerService.updateFlightEnergies(energyCache.values)
            }.let {
                logger.info("同步飞行能量成功,${it.value}条数据已更新，耗时${it.duration}")
            }
            syncing.set(false)
        }
    }

    suspend fun Player.updateEnergy(energy: Double) {
        this.energy = energy
        PlayerService.updateFlightEnergy(energyCache[this]!!)
    }

    /**
     * 由单独线程控制循环
     * 每秒执行一次
     */
    private fun tick() {
        onlinePlayers.filter {
            it.gameMode == GameMode.SURVIVAL && it.energy > 0.0
        }.filter {
            energyBar[it]!!.setVisible(it.isFlying)
            it.isFlying
        }.forEach { player ->
            player.energy = (player.energy - (ENERGY_COST)).coerceAtLeast(0.0)
            energyBar[player]!!.tick()
            if (player.energy <= 0.0) {
                toggleFly(player, false)
                player.sendMessage("§c飞行能量已耗尽")

                player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 400, 1, false, true))
            }
        }
    }

}

