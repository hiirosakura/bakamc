package cn.bakamc.folia.flight_energy

import cn.bakamc.common.text.bakatext.BakaText
import cn.bakamc.folia.config.FlightEnergyConfig.ALLOW_FLY_WORLD
import cn.bakamc.folia.config.FlightEnergyConfig.CLOSE_ADVENTURE_PLAYERS_FLYING
import cn.bakamc.folia.config.FlightEnergyConfig.ENERGY_COST
import cn.bakamc.folia.config.FlightEnergyConfig.FORBID_FLY_WORLD_MESSAGE
import cn.bakamc.folia.config.FlightEnergyConfig.MONEY_ITEM
import cn.bakamc.folia.config.FlightEnergyConfig.SYNC_PERIOD
import cn.bakamc.folia.config.FlightEnergyConfig.TICK_PERIOD
import cn.bakamc.folia.db.table.FlightEnergy
import cn.bakamc.folia.db.table.SpecialItem
import cn.bakamc.folia.extension.onlinePlayers
import cn.bakamc.folia.extension.uuid
import cn.bakamc.folia.item.SpecialItemManager
import cn.bakamc.folia.service.PlayerService
import cn.bakamc.folia.util.*
import kotlinx.coroutines.runBlocking
import moe.forpleuvoir.nebula.common.api.Initializable
import moe.forpleuvoir.nebula.common.color.Colors
import net.minecraft.server.level.ServerPlayer
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.jetbrains.annotations.Contract
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

object FlightEnergyManager : Listener, Initializable {

    private lateinit var energyCache: MutableMap<Player, FlightEnergy>

    private lateinit var energyBarCache: MutableMap<Player, EnergyBar>

    private lateinit var tasks: List<AsyncTask>

    private var syncing = AtomicBoolean(false)

    val Player.flightEnergy: FlightEnergy?
        get() = energyCache[this]

    val Player.energyBar: EnergyBar?
        get() = energyBarCache[this]

    /**
     * 玩家当前的飞行能量
     */
    var Player.energy: Double
        get() {
            return energyCache[this]?.energy ?: runBlocking { PlayerService.getFlightEnergy(this@energy).energy }
        }
        set(value) {
            energyCache[this]?.energy = value.coerceAtLeast(0.0)
            if (value <= 0.0) {
                toggleFly(this, false)
            }
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

        energyBarCache = ConcurrentHashMap()

        runBlocking {
            energyCache.putAll(PlayerService.getFlightEnergies(onlinePlayers))
            energyCache.forEach { (player, flightEnergy) ->
                energyBarCache[player] = EnergyBar.create(player, flightEnergy)
            }
            logger.info("飞行能量加载完成")
        }

    }

    fun onDisable() {
        if (this::energyCache.isInitialized) {
            if (!syncing.get()) sync()
            energyCache.clear()
            energyBarCache.forEach { (player, bar) ->
                bar.close()
            }
            energyBarCache.clear()
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
        energyBarCache[player] = EnergyBar.create(player, energyCache[player]!!)

        if (player.allowFly()) {
            player.flightEnergy?.let {
                player.allowFlight = it.enabled
                if (!player.isOnGround) player.isFlying = it.enabled
            }
        } else if (!player.inAllowFlyWorld) player.sendMessage(forbidFlyWorldMessage)

    }

    suspend fun onPlayerQuit(player: Player) {
        PlayerService.updateFlightEnergy(energyCache[player]!!)
        energyCache.remove(player)
        energyBarCache.remove(player)?.close()
    }

    fun onWorldChanged(event: PlayerChangedWorldEvent) {
        val player = event.player
        if (!player.allowFly()) {
            if (!player.inAllowFlyWorld) player.sendMessage(forbidFlyWorldMessage)
            toggleFly(player, false)
        }
    }

    fun onPlayerRespawn(player: Player) {
        if (player.allowFly()) {
            player.flightEnergy?.let {
                if (it.enabled) {
                    player.allowFlight = true
                }
            }
        } else {
            if (!player.inAllowFlyWorld) player.sendMessage(forbidFlyWorldMessage)
        }
    }

    fun onPlayerGameModeChange(player: Player, newGameMode: GameMode) {
        if (newGameMode == GameMode.SURVIVAL) {
            val isFlying = player.isFlying
            player.execute(1) {
                player.allowFlight = player.flightEnergy?.enabled ?: false
                if (player.allowFlight) player.isFlying = isFlying
            }
        } else {
            player.energyBar?.setVisible(false)
        }
    }

    fun addAllOnlinePlayerEnergy(energy: Double, range: ClosedRange<Double>): Int {
        energyCache.values.forEach {
            it.energy = (it.energy + energy).coerceIn(range)
        }
        if (syncing.get()) {
            runDelayed(1.seconds) {
                sync()
            }
        }
        return energyCache.size
    }

    /**
     * 切换飞行状态
     * @param player Player
     * @param enabled Boolean?
     */
    @Contract("_, null -> !enabled")
    fun toggleFly(player: Player, enabled: Boolean? = null) {
        player.flightEnergy?.let {
            if (enabled ?: !it.enabled)
                player.enableFly()
            else
                player.disableFly()
        }
    }

    private fun Player.enableFly() {
        if (!allowFly()) {
            if (!inAllowFlyWorld) sendMessage(forbidFlyWorldMessage)
            return
        }
        val flightEnergy = flightEnergy
        flightEnergy?.enabled = true
        if (flightEnergy?.enabled != true) energyBar?.setVisible(false)
        if (gameMode in arrayOf(GameMode.SURVIVAL, GameMode.ADVENTURE)) {
            allowFlight = flightEnergy?.enabled ?: false
        }
    }

    private fun Player.disableFly() {
        flightEnergy?.enabled = false
        energyBar?.setVisible(false)
        if (gameMode in arrayOf(GameMode.SURVIVAL, GameMode.ADVENTURE)) {
            allowFlight = false
            isFlying = false
        }
    }

    private val forbidFlyWorldMessage get() = BakaText.parse(FORBID_FLY_WORLD_MESSAGE)

    /**
     * 检查玩家是否可以被设置为飞行
     * @receiver Player
     * @return Boolean
     */
    private fun Player.allowFly(): Boolean {
        return inAllowFlyWorld && this.gameMode == GameMode.SURVIVAL && energy > 0
    }

    private val Player.inAllowFlyWorld: Boolean get() = world.name in ALLOW_FLY_WORLD

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
        val old = this.energy
        this.energy = energy
        measureTime {
            PlayerService.updateFlightEnergy(energyCache[this]!!)
        }.let {
            logger.info("玩家[${name}]飞行能量更新[${old} -> ${this.energy}],耗时$it")
        }
    }

    /**
     * 由单独线程控制循环
     * 每秒执行一次
     */
    private fun tick() {
        if (CLOSE_ADVENTURE_PLAYERS_FLYING) {
            onlinePlayers.filter { player -> player.gameMode == GameMode.ADVENTURE && player.flightEnergy!!.enabled }.forEach { player ->
                player.execute {
                    player.flightEnergy!!.enabled = false
                    player.allowFlight = false
                }
            }
        }
        onlinePlayers.filter { player ->
            //是否需要扣除飞行能量
            (player.gameMode in arrayOf(GameMode.SURVIVAL, GameMode.ADVENTURE)) && player.energy > 0.0 && player.vehicle == null
        }.filter { player ->
            //是否在可以飞行的世界且已经开了飞行
            if (!player.inAllowFlyWorld && player.flightEnergy!!.enabled) {
                player.disableFly()
                player.sendMessage(forbidFlyWorldMessage)
                return@filter false
            }
            //将飞行能量条可见设置为当前飞行状态
            player.energyBar?.setVisible(player.isFlying)
            player.isFlying
        }.forEach { player ->
            runCatching {
                //更新飞行能量
                player.energy = (player.energy - (ENERGY_COST)).coerceAtLeast(0.0)
                //更新飞行能量条状态
                player.energyBar?.tick()
                //如果飞行能量耗尽
                if (player.energy <= 0.0) {
                    //关闭飞行
                    toggleFly(player, false)
                    player.sendMessage(literalText("飞行能量已耗尽", Style(Colors.RED)))
                    //给予200tick的缓降效果
                    player.execute {
                        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 200, 1, false, true))
                    }

                }
            }.onFailure {
                logger.warn("玩家[${player.name}]飞行能量更新失败", it)
            }
        }
    }


}

