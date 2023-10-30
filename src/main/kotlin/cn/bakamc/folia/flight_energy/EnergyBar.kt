package cn.bakamc.folia.flight_energy

import cn.bakamc.folia.config.Configs.FlightEnergy.ENERGY__BAR__COLOR
import cn.bakamc.folia.config.Configs.FlightEnergy.ENERGY__BAR__TITLE
import cn.bakamc.folia.config.Configs.FlightEnergy.MAX_ENERGY
import cn.bakamc.folia.flight_energy.FlightEnergyManager.energy
import org.bukkit.NamespacedKey
import org.bukkit.Server
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
        bar = server.createBossBar(key, title(), ENERGY__BAR__COLOR, BarStyle.SEGMENTED_10)
        bar.isVisible = false
        bar.progress = energy() / maxEnergy()
        bar.addPlayer(player)
    }

    fun tick() {
        bar.setTitle(title())
        bar.color = ENERGY__BAR__COLOR
        bar.progress = energy() / maxEnergy()
    }

    private fun title(): String {
        return ENERGY__BAR__TITLE.format(energy(), maxEnergy())
    }

    fun setVisible(visible: Boolean) {
        bar.isVisible = visible
    }

    fun close() {
        server.removeBossBar(key)
    }
}