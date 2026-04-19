package cn.bakamc.folia.evnet

import cn.bakamc.folia.BakaMC
import cn.bakamc.folia.config.{FlightEnergyConfig, MiscConfig}
import cn.bakamc.folia.database.services.PlayerService
import cn.bakamc.folia.functional.flightenergy.FlightEnergyManager
import org.bukkit.event.player.*
import org.bukkit.event.{EventHandler, Listener}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object PlayerEventListener extends Listener {

  @EventHandler
  def onPlayerJoin(event: PlayerJoinEvent): Unit = {
    if (!MiscConfig.enablePlayerJoinMessage) event.joinMessage(null)
    PlayerService.insertOrUpdate(player = event.getPlayer).onComplete {
      case Success(value) =>
        Future {
          FlightEnergyManager.onPlayerJoin(event.getPlayer)
        }.onComplete {
          case Success(value) =>
            BakaMC.logger.info(s"${event.getPlayer.getName} 加入服务器")
          case Failure(exception) =>
            BakaMC.logger.error(s"${event.getPlayer.getName} 加载时数据库错误", exception)
        }
      case Failure(exception) => BakaMC.logger.error(s"${event.getPlayer.getName} 数据库错误", exception)
    }
  }

  @EventHandler
  def onPlayerQuit(event: PlayerQuitEvent): Unit = {
    if (!MiscConfig.enablePlayerJoinMessage)
      event.quitMessage(null)

    FlightEnergyManager.onPlayerQuit(event.getPlayer) {
      case Success(value) =>
        BakaMC.logger.info(s"${event.getPlayer.getName} 退出服务器")
      case Failure(exception) => BakaMC.logger.error(s"${event.getPlayer.getName} 退出服务器时数据库错误", exception)
    }
  }

  @EventHandler
  def onChangeWorld(event: PlayerChangedWorldEvent): Unit = {
    FlightEnergyManager.onWorldChanged(event.getPlayer)
  }

  @EventHandler
  def onPlayerRespawn(event: PlayerRespawnEvent): Unit = {
    FlightEnergyManager.onPlayerRespawn(event.getPlayer)
  }

  @EventHandler
  def onPlayerGameModeChange(event: PlayerGameModeChangeEvent): Unit = {
    FlightEnergyManager.onPlayerGameModeChanged(event.getPlayer, event.getNewGameMode)
  }

}
