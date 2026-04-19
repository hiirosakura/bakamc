package cn.bakamc.folia.database.table

import org.bukkit.entity.Player
import slick.jdbc.MySQLProfile.api.*

object FlightEnergy {

  def apply(player: Player, energy: Double, enabled: Boolean, barVisible: Boolean): FlightEnergy =
    FlightEnergy(player.getUniqueId.toString, energy, enabled, barVisible)

}

case class FlightEnergy(
  playerUuid: String,
  var energy: Double,
  var enabled: Boolean,
  var barVisible: Boolean
)

class FlightEnergies(tag: Tag) extends Table[FlightEnergy](tag, "flight_energy") {
  def playerUuid = column[String]("player_uuid", O.PrimaryKey, O.Length(36))

  def energy = column[Double]("energy")

  def enabled = column[Boolean]("enabled")

  def barVisible = column[Boolean]("bar_visible")

  def * = (playerUuid, energy, enabled, barVisible).mapTo[FlightEnergy]
}

val flightEnergies = TableQuery[FlightEnergies]
