package cn.bakamc.folia.flight_energy

import cn.bakamc.common.text.bakatext.BakaText
import cn.bakamc.folia.config.FlightEnergyConfig
import cn.bakamc.folia.config.FlightEnergyConfig.MAX_ENERGY
import cn.bakamc.folia.db.table.FlightEnergy
import cn.bakamc.folia.util.execute
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class EnergyBar private constructor(
    private val player: Player,
    private val flightEnergy: FlightEnergy,
) {

    private var lastEnergy = flightEnergy.energy

    companion object {
        fun create(player: Player, flightEnergy: FlightEnergy): EnergyBar {
            return EnergyBar(player, flightEnergy)
        }
    }

    private var bar: BossBar

    init {
        bar = BossBar.bossBar(name(), progress, FlightEnergyConfig.EnergyBar.COLOR, BossBar.Overlay.NOTCHED_6)
        player.execute {
            player.hideBossBar(bar)
        }
    }

    fun tick() {
        player.execute {
            bar.name(name())
            bar.progress(progress)
            lastEnergy = flightEnergy.energy
        }
    }

    private val progress get() = (flightEnergy.energy / MAX_ENERGY).coerceIn(0.0, 1.0).toFloat()

    private fun name(): Component {
        return BakaText.parse(FlightEnergyConfig.EnergyBar.TITLE.format(flightEnergy.energy, flightEnergy.energy - lastEnergy, MAX_ENERGY))
    }

    fun setVisible(visible: Boolean) {
        player.execute {
            if (visible && flightEnergy.barVisible) {
                player.showBossBar(bar)
            } else
                player.hideBossBar(bar)
        }
    }

    fun close() {
        player.execute {
            player.hideBossBar(bar)
        }
    }
}