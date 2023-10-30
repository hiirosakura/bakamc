package cn.bakamc.folia.flight_energy

import cn.bakamc.folia.config.Configs.FlightEnergy.MAX_ENERGY
import cn.bakamc.folia.flight_energy.FlightEnergyManager.energy
import org.bukkit.NamespacedKey
import org.bukkit.Server
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.KeyedBossBar
import org.bukkit.entity.Player

class EnergyBar private constructor(
    private val server: Server,
    private val player: Player,
    private val energy: () -> Double,
    private val maxEnergy: () -> Double
) {

    companion object {
        fun create(server: Server, player: Player, energy: () -> Double = { player.energy }, maxEnergy: () -> Double = { MAX_ENERGY }): EnergyBar {
            return EnergyBar(server, player, energy, maxEnergy)
        }
    }

    private var bar: KeyedBossBar

    var key: NamespacedKey = NamespacedKey.minecraft(player.name)

    init {
        //TODO 颜色改成可配置
        bar = server.createBossBar(key, title(), BarColor.GREEN, BarStyle.SEGMENTED_10)
        bar.progress = energy() / maxEnergy()
        bar.addPlayer(player)
    }

    fun tick() {
        bar.setTitle(title())
        bar.progress = energy() / maxEnergy()
    }

    private fun title(): String {
        //TODO 文本改成可配置
        return "飞行能量: ${"%.2f".format(energy())}/${"%.2f".format(maxEnergy())}"
    }

    fun close() {
        server.removeBossBar(key)
    }
}