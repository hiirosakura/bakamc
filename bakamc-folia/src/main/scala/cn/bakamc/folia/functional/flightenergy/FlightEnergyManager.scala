package cn.bakamc.folia.functional.flightenergy

import cn.bakamc.folia.api.Reloadable
import cn.bakamc.folia.config.FlightEnergyConfig.*
import cn.bakamc.folia.database.services.PlayerService
import cn.bakamc.folia.database.table.FlightEnergy
import cn.bakamc.folia.functional.flightenergy.EnergyBar as _EnergyBar
import cn.bakamc.folia.util.AsyncTask
import cn.bakamc.folia.{BakaMC, execute, onlinePlayers}
import moe.forpleuvoir.nebula.common.api.Initializable
import moe.forpleuvoir.nebula.common.util.measureTime
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import org.bukkit.potion.{PotionEffect, PotionEffectType}
import org.bukkit.{GameMode, Sound as Sounds}

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationDouble}
import scala.jdk.CollectionConverters.CollectionHasAsScala

object FlightEnergyManager extends Initializable, Reloadable {

  private[flightenergy] given [T] => Conversion[T, Option[T]] = Some(_)

  private def logger = BakaMC.logger

  private[flightenergy] val energyCache = ConcurrentHashMap[Player, FlightEnergy]()

  private[flightenergy] val barCache = ConcurrentHashMap[Player, EnergyBar]()

  private val tasks = List(
    AsyncTask(0.second, tickPeriod) { it => tick() },
    AsyncTask(0.second, syncPeriod) { it => sync() }
  )

  private[flightenergy] val allowGameMode = List(GameMode.SURVIVAL, GameMode.ADVENTURE)
  private[flightenergy] val timeOutDuration: Duration = 10.seconds

  given Conversion[AtomicBoolean, Boolean] = _.get()

  private[flightenergy] val syncing = AtomicBoolean(false)

  export FlightEnergyPlayerOps._

  export FlightEnergyCommandOps._

  override def initialization(): Unit = {
    syncing.set(false)

    tasks.foreach(_.run())

    energyCache.clear()
    barCache.clear()

    try {
      val result = Await.result(PlayerService.getFlightEnergies(onlinePlayers), timeOutDuration)
      result.foreachEntry { (player, energy) =>
        energyCache.put(player, energy)
        barCache.put(player, _EnergyBar(player, energy))
      }
      logger.info(s"同步玩家飞行能量成功,在线玩家: ${onlinePlayers.size},同步玩家: ${result.size}")
    } catch {
      case e: Throwable => logger.error(s"同步玩家飞行能量失败: ${e.getMessage}")
    }
  }

  def onDisable(): Unit = {
    if (!syncing) sync()
    energyCache.clear()
    barCache.forEach((player, bar) => bar.close())
    barCache.clear()
    tasks.foreach(_.cancel())
  }

  override def reload(): Unit = {
    onDisable()
    initialization()
  }

  private[flightenergy] def sync(): Unit = {
    if (syncing) return
    syncing.set(true)
    measureTime {
      try Await.result(PlayerService.updateFlightEnergies(energyCache.values.asScala), timeOutDuration)
      catch {
        case e: Throwable => logger.error(s"玩家飞行能量同步失败: ${e.getMessage}")
          None
      } finally {
        syncing.set(false)
      }
    }.foreach { (result, time) =>
      if (result.exists(_ > 0)) {
        logger.info(s"玩家飞行能量同步成功,同步玩家: ${result.size},同步耗时: ${time.toMillis}ms")
      }
    }
  }

  private def tick(): Unit = {
    onlinePlayers.foreach { player =>
      val gameMode = player.getGameMode //玩家游戏模式
      val isFlying = player.isFlying //玩家是否正在飞行
      val isEnabled = player.flightEnergy.exists(_.enabled) //玩家是否开启了飞行

      //如果禁止冒险模式飞行&玩家处于冒险模式,并且玩家开启了插件的飞行开关,关闭玩家所有飞行
      if (disableAdventureModeFly && gameMode == GameMode.ADVENTURE && isEnabled) {
        player.execute() {
          player.flightEnergy.foreach(_.enabled = false) //关闭插件的飞行开关
          player.setAllowFlight(false) //关闭原版的飞行开关
        }
      }

      //是否在可以飞行的世界
      val inAllowFlyWorld = player.inAllowFlyWorld
      val shouldShowBar = allowGameMode.contains(gameMode) && // 是否处于冒险模式和生存模式
        player.getVehicle == null && //不能处于骑乘状态下
        player.energy > 0.0 && //有飞行能量
        inAllowFlyWorld //是否在可以飞行的世界

      //更新飞行能量条显示状态
      player.energyBar.foreach(_.visible = shouldShowBar && isFlying)

      if (shouldShowBar) {
        //是否在可以飞行的世界且已经开了飞行
        if (!inAllowFlyWorld && isEnabled) {
          //关闭飞行并提醒玩家
          player.disableFly()
          player.sendMessage(forbidFlyWorldMessageComponent)
        }
        //正在飞行 处理扣费
        else if (isFlying) {
          try {
            //扣除能量
            player.energy = player.energy - energyCost
            //更新能量条状态
            player.energyBar.foreach(_.tick())
            //玩家能量小于等于0
            if (player.energy <= 0.0) {
              //关闭飞行
              player.toggleFlyState(false)
              //提醒玩家
              player.sendMessage(Component.text("飞行能量已耗尽").color(TextColor.color(0xFF0000)))
              logger.info(s"玩家 ${player.getName} 的飞行能量已耗尽")
              //给玩家添加200tick的缓降效果
              player.execute() {
                player.playSound(Sound.sound(Sounds.BLOCK_ANVIL_LAND, Sound.Source.UI, 1.0f, 1.0f))
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 200, 0))
              }
            }
          } catch {
            case e: Throwable => logger.warn(s"玩家 ${player.getName} 飞行能量更新失败: ${e.getMessage}")
          }
        }
      }
    }

  }

}




