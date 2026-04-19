package cn.bakamc.folia.functional.flightenergy

import cn.bakamc.common.inlinestyletext.InlineStyleTextParser
import cn.bakamc.folia.config.FlightEnergyConfig
import cn.bakamc.folia.database.table.FlightEnergy
import cn.bakamc.folia.execute
import moe.forpleuvoir.nebula.common.util.primitive.CoerceInExtension.clamp
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

case class EnergyBar(
  private val player: Player,
  private val energy: FlightEnergy,
) {

  private var lastEnergy: Double = energy.energy

  private val bar: BossBar = BossBar.bossBar(name, progress, FlightEnergyConfig.EnergyBar.color, FlightEnergyConfig.EnergyBar.style)

  def name: Component = {
    val content = FlightEnergyConfig.EnergyBar.title.value.formatted(energy.energy, energy.energy - lastEnergy, FlightEnergyConfig.maxEnergy.value)
    InlineStyleTextParser.NONE_EVENT.parse(content)
  }

  def progress: Float = (energy.energy / FlightEnergyConfig.maxEnergy).clamp(0.0, 1.0).toFloat

  player.execute() {
    player.hideBossBar(bar)
  }

  def tick(): Unit = {
    player.execute() {
      bar.name(name)
      bar.progress(progress)
      lastEnergy = energy.energy
    }
  }

  def visible: Boolean = energy.barVisible

  def visible_=(visible: Boolean): Unit = {
    player.execute() {
      if (visible && energy.barVisible) player.showBossBar(bar)
      else player.hideBossBar(bar)
    }
  }

  def close(): Unit = {
    player.execute() {
      player.hideBossBar(bar)
    }
  }

}
