package cn.bakamc.folia.database.services

import cn.bakamc.folia.database.db
import cn.bakamc.folia.database.table.{FlightEnergy, flightEnergies, playerInfos}
import org.bukkit.entity.Player
import slick.jdbc.MySQLProfile.api.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PlayerService {

  def insertOrUpdate(player: Player): Future[Int] = db {
    playerInfos.insertOrUpdate(player)
  }

  def getFlightEnergy(player: Player): Future[FlightEnergy] = db {
    flightEnergies.filter(_.playerUuid === player.getUniqueId.toString)
      .result.headOption
      .flatMap {
        case Some(energy) => DBIO.successful(energy)
        case None =>
          val newEnergy = FlightEnergy(player, 0.0, false, true)
          (flightEnergies += newEnergy) >> DBIO.successful(newEnergy)
      }
  }

  def getFlightEnergies(players: Iterable[Player]): Future[Map[Player, FlightEnergy]] = {
    if (players.isEmpty) Future.successful(Map.empty)
    else {
      val playerMap = players.map(p => p.getUniqueId.toString -> p).toMap
      db {
        val playerUuids = players.map(_.getUniqueId.toString)
        flightEnergies.filter(_.playerUuid inSet playerUuids)
          .result
          .map(energies =>
            energies.map(e => playerMap(e.playerUuid) -> e).toMap
          )
      }
    }
  }

  def updateFlightEnergy(energy: FlightEnergy): Future[Int] = db {
    flightEnergies.insertOrUpdate(energy)
  }

  def updateFlightEnergies(energies: Iterable[FlightEnergy]): Future[Option[Int]] = {
    if (energies.isEmpty) Future.successful(None)
    else db {
      flightEnergies.insertOrUpdateAll(energies)
    }
  }

  def addAllPlayerEnergy(amount: Double, range: (Double, Double)): Future[Option[Int]] = {
    val (min, max) = range
    if (amount <= 0) Future.successful(None)
    else {
      val sql =
        sqlu"""
            UPDATE flight_energy
            WHERE energy <= $max
            SET energy = CASE
              WHEN energy + $amount > $max THEN $max
              WHEN energy + $amount < $min THEN $min
              ELSE energy + $amount
            END
          """
      db(sql).map(count => Some(count))
    }
  }

}