package cn.bakamc.folia.functional.flightenergy

import cn.bakamc.common.comp
import cn.bakamc.common.extension.Text
import cn.bakamc.common.extension.Text.given
import cn.bakamc.folia.BakaMC
import cn.bakamc.folia.command.dsl.ContextOps.feedback
import cn.bakamc.folia.config.FlightEnergyConfig.maxEnergy
import cn.bakamc.folia.functional.flightenergy.FlightEnergyManager.*
import cn.bakamc.folia.functional.flightenergy.PlayerFlightEnergyOps.energy
import cn.bakamc.folia.util.text.PluginComponentAdapter.given
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import moe.forpleuvoir.nebula.common.color.Colors
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

object FlightEnergyCommandOps {

  private def logger = BakaMC.logger

  def info(player: Player)(using context: CommandContext[CommandSourceStack]): Unit = {
    val flightEnergy = player.flightEnergy match {
      case Some(value) => feedback(comp"${player} => 当前飞行状态${Text.status(player.getAllowFlight)}, 飞行能量[${value.energy}], 是否开启飞行${Text.status(value.enabled)}, 能量条显示${Text.status(value.barVisible)}")
      case None => 0
    }
  }

  def toggleFly(player: Player, status: Option[Boolean])(using context: CommandContext[CommandSourceStack]): Unit = {
    if (player.energy > 0) {
      player.toggleFlyState(status)
      val statusDisplay = Text.status(player.getAllowFlight)
      feedback(comp"飞行状态已切换:$statusDisplay")
    } else {
      feedback(comp"您的飞行能量不足,无法开启飞行状态".color(0xFF5555))
      val cmdText = Text.literal("/fly recharge <飞行能量数量>").color(Colors.AQUA).clickEvent(ClickEvent.suggestCommand("/fly recharge "))
      feedback(comp"请使用指令 $cmdText 购买飞行能量".color(0x55FF55))
    }
  }

  def toggleBarVisible(player: Player, status: Option[Boolean])(using context: CommandContext[CommandSourceStack]): Unit = {
    status match {
      case Some(value) => player.barVisible = value
      case None => player.barVisible = !player.barVisible
    }
    val statusDisplay = Text.status(player.barVisible)
    feedback(comp"飞行能量条显示已切换:$statusDisplay")
  }

  def updateOnlinePlayerEnergy(operator: CommandSender, players: Seq[Player], isBounded: Boolean)
    (updateOp: (Double, Double => Unit) => Unit)
    (using context: CommandContext[CommandSourceStack]): Unit = {
    val cache = energyCache.asScala
    val changeList = mutable.ListBuffer.empty[(Player, Double, Double)]
    players.foreach { p =>
      cache.find(_._1.uuid == p.uuid).map(_._2) match {
        case Some(flightEnergy) =>
          val old = p.energy
          if (isBounded) {
            updateOp(old, { newEnergy => p.energy = newEnergy.max(0.0).min(maxEnergy) })
          } else {
            updateOp(old, p.energy = _)
          }
          p.sendMessage(comp"飞行能量已更新[$old -> ${p.energy}]")
          changeList.addOne((p, old, p.energy))
        case None =>
      }
    }
    feedback(comp"成功更新${changeList.size}名玩家的飞行能量")
    logger.info(s"操作者:${operator.getName},更新玩家飞行能量列表:[${changeList.map(x => s"${x._1.getName}(${x._1.uuid}):${x._2} -> ${x._3}").mkString(", ")}]")
    if (changeList.size != players.size) {
      feedback(comp"${players.size - changeList.size}名玩家的飞行能量更新失败")
      logger.info(s"操作者:${operator.getName},更新失败玩家列表:[${players.filterNot(x => changeList.exists(_._1 == x)).map(x => s"${x.getName}(${x.uuid}):未在缓存中找到该玩家").mkString(", ")}]")
    }
  }

}
