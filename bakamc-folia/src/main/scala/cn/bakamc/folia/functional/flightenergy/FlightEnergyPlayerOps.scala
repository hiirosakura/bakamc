package cn.bakamc.folia.functional.flightenergy

import cn.bakamc.folia.config.FlightEnergyConfig.{allowFlyWorld, forbidFlyWorldMessageComponent, name}
import cn.bakamc.folia.database.services.PlayerService
import cn.bakamc.folia.database.table.FlightEnergy
import cn.bakamc.folia.functional.flightenergy.FlightEnergyManager.*
import cn.bakamc.folia.util.runDelayed
import cn.bakamc.folia.{BakaMC, execute}
import moe.forpleuvoir.nebula.common.util.measureTime
import moe.forpleuvoir.nebula.common.util.primitive.CoerceInExtension.clamp
import org.bukkit.GameMode
import org.bukkit.Statistic.PLAY_ONE_MINUTE
import org.bukkit.entity.Player

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt, FiniteDuration}
import scala.util.{Failure, Success, Try}

object FlightEnergyPlayerOps {

  private def logger = BakaMC.logger

  given [T] => Conversion[T, Option[T]] = Some[T]

  def onPlayerJoin(player: Player): Unit = {
    energyCache.put(player, Await.result(PlayerService.getFlightEnergy(player), timeOutDuration))
    barCache.put(player, EnergyBar(player, energyCache.get(player)))

    if (player.allowFly) {
      player.flightEnergy.foreach { it =>
        player.setAllowFlight(it.enabled)
        //noinspection ScalaDeprecation
        if (!player.isOnGround) player.setFlying(it.enabled)
      }
    } else if (!player.inAllowFlyWorld) player.sendMessage(forbidFlyWorldMessageComponent)
  }

  def onPlayerQuit(player: Player)(_result: Try[Int] => Unit): Unit = {
    PlayerService.updateFlightEnergy(energyCache.get(player)).onComplete { result =>
      result match {
        case Success(it) => logger.info(s"更新玩家 ${player.getName} 飞行能量成功")
        case Failure(exception) => logger.error(s"更新玩家 ${player.getName} 飞行能量失败: ${exception.getMessage}")
      }
      _result(result)
    }
  }

  def onWorldChanged(player: Player): Unit = {
    if (!player.allowFly) {
      if (!player.inAllowFlyWorld) player.sendMessage(forbidFlyWorldMessageComponent)
      player.toggleFlyState(false)
    }
  }

  def onPlayerRespawn(player: Player): Unit = {
    if (player.allowFly) {
      player.flightEnergy.foreach { it =>
        if (it.enabled) player.setAllowFlight(true)
      }
    } else {
      if (!player.inAllowFlyWorld) player.sendMessage(forbidFlyWorldMessageComponent)
    }
  }

  def onPlayerGameModeChanged(player: Player, newGameMode: GameMode): Unit = {
    if (newGameMode == GameMode.SURVIVAL) {
      val isFlying = player.isFlying
      player.execute() {
        player.setAllowFlight(player.flightEnergy.exists(_.enabled))
        if (player.getAllowFlight) player.setFlying(isFlying)
      }
    } else {
      player.energyBar.foreach(_.visible = false)
    }
  }

  def addAllOnlinePlayerEnergy(energy: Double, range: (Double, Double)): Int = {
    energyCache.values().forEach { it =>
      it.energy = (it.energy + energy).clamp(range)
    }
    if (syncing.get()) {
      runDelayed(1.seconds) { it =>
        sync()
      }
    }
    logger.info(s"所有玩家的飞行能量增加 $energy,影响的玩家数量: ${energyCache.size()}")
    energyCache.size()
  }

  //region Player Extension
  extension (player: Player) {

    def uuid: String = player.getUniqueId.toString

    def flightEnergy: Option[FlightEnergy] = Option(energyCache.get(player))

    def updateEnergy(energy: Double): Unit = {
      val old = player.energy
      player.energy_=(energy)
      measureTime {
        try Some(Await.result(PlayerService.updateFlightEnergy(flightEnergy.get), timeOutDuration))
        catch {
          case e: Exception =>
            logger.error(s"更新玩家 ${player.getName} 飞行能量失败: ${e.getMessage}")
            None
        }
      }.foreach { (result, time) =>
        if (result.exists(_ > 0)) {
          logger.info(s"玩家[$name]飞行能量更新[$old -> ${player.energy}], 耗时${time.toMillis}ms")
        }
      }
    }

    def energyBar: Option[EnergyBar] = Option(barCache.get(player))

    def barVisible: Boolean = {
      flightEnergy match {
        case Some(energy) => energy.barVisible
        case None =>
          val result = Await.result(PlayerService.getFlightEnergy(player), timeOutDuration)
          energyCache.put(player, result)
          result.barVisible
      }
    }

    def barVisible_=(visible: Boolean): Unit = {
      flightEnergy match {
        case Some(energy) => energy.barVisible = visible
        case None =>
          val result = Await.result(PlayerService.getFlightEnergy(player), timeOutDuration)
          energyCache.put(player, result)
          result.barVisible = visible
      }
    }

    def energy: Double = {
      try {
        energyCache.getOrDefault(player, Await.result(PlayerService.getFlightEnergy(player), 5.seconds)).energy
      } catch {
        case e: Exception =>
          logger.error(s"获取玩家 ${player.getName} 飞行能量失败: ${e.getMessage}")
          0.0
      }
    }

    def energy_=(energy: Double): Unit = {
      flightEnergy.foreach(_.energy = energy.max(0.0))
      if (energy <= 0) {
        toggleFlyState(true)
      }
    }

    def inAllowFlyWorld: Boolean = allowFlyWorld.contains(player.getWorld.getName)

    def allowFly: Boolean = inAllowFlyWorld && player.getGameMode == GameMode.SURVIVAL && energy > 0

    def toggleFlyState(enabled: Option[Boolean] = None): Unit = {
      player.flightEnergy.foreach { e =>
        if (enabled.getOrElse(!e.enabled)) {
          player.enableFly()
        } else {
          player.disableFly()
        }
      }
    }

    def enableFly(): Unit = {
      if (!allowFly) {
        if (!inAllowFlyWorld) player.sendMessage(forbidFlyWorldMessageComponent)
        return
      }
      val energy = flightEnergy
      energy.foreach(_.enabled = true)
      if (energy.exists(!_.enabled)) energyBar.foreach(_.visible = false)
      if (allowGameMode.contains(player.getGameMode)) {
        player.setAllowFlight(energy.exists(_.enabled))
      }
    }

    def disableFly(): Unit = {
      flightEnergy.foreach(_.enabled = false)
      energyBar.foreach(_.visible = false)
      if (allowGameMode.contains(player.getGameMode)) {
        player.setAllowFlight(false)
        player.setFlying(false)
      }
    }

    /**
     * @return 玩家在线时长
     */
    def onlineDuration: FiniteDuration =
      Duration(player.getStatistic(PLAY_ONE_MINUTE) / 20, TimeUnit.SECONDS)

  }
  //endregion

}
