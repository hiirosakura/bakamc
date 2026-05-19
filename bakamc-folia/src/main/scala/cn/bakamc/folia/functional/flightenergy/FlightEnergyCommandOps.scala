package cn.bakamc.folia.functional.flightenergy

import cn.bakamc.common.comp
import cn.bakamc.common.extension.Text
import cn.bakamc.common.extension.Text.given
import cn.bakamc.folia.BakaMC
import cn.bakamc.folia.command.dsl.ContextOps.*
import cn.bakamc.folia.config.FlightEnergyConfig.{energyPrice, energyPriceMap, maxEnergy, moneyItem, onlineDiscount}
import cn.bakamc.folia.util.player.PlayerVaultOps.*
import cn.bakamc.folia.database.table.SpecialItem
import cn.bakamc.folia.functional.flightenergy.FlightEnergyManager.*
import cn.bakamc.folia.functional.flightenergy.FlightEnergyPlayerOps.energy
import cn.bakamc.folia.functional.specialitem.SpecialItemManager
import cn.bakamc.folia.util.text.PluginComponentAdapter.given
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import moe.forpleuvoir.nebula.common.color.Colors
import net.kyori.adventure.text.event.ClickEvent
import net.milkbowl.vault2.economy.EconomyResponse.ResponseType
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.jdk.CollectionConverters.*
import scala.language.implicitConversions
import scala.util.{Failure, Success}

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

  def recharge(player: Player)(using context: CommandContext[CommandSourceStack]): Unit = {
    //货币类型
    val currency = getArg[String]("currency")
    //玩家所持有的货币总量
    val totalMoney = player.money(Some(currency))
    //需要充值的能量
    val energy = getArg[Double]("energy").max(0.0)
    //折扣
    val discount = onlineDiscount(player.onlineDuration)
    //本次重置需要消耗的货币
    val cost = energy * energyPrice(currency) * discount
    //是否有充足的货币
    if (totalMoney >= cost) {
      //是否超出能量持有上限
      if (energy + player.energy > maxEnergy) {
        feedback(comp"超出了能量上限，最大值:[$maxEnergy],充值后会超出上限:[${energy + player.energy - maxEnergy}]")
      } else {
        try {
          val response = player.withdraw(cost, Some(currency))

          response.`type` match {
            case ResponseType.SUCCESS =>
              Future {
                player.updateEnergy((player.energy + energy).max(0.0).min(maxEnergy))
              }.onComplete {
                case Success(value) =>
                  feedback(comp"成功购买[${energy}]的能量,当前剩余能量值[${player.energy}]")
                  logger.info(s"玩家(${player.getName}[${player.uuid}])成功购买飞行能量:$energy,花费$cost$currency,货币变化:$totalMoney -> ${response.balance.doubleValue()}")
                case Failure(exception) =>
                  feedback(comp"购买失败,未知错误请联系管理员")
                  logger.error(s"玩家[${player.getName}]购买飞行能量失败", exception)
              }
            case ResponseType.FAILURE =>
              feedback(comp"购买失败[${response.errorMessage}]")
              logger.error(s"玩家[${player.getName}]购买飞行能量失败, msg: ${response.errorMessage}")

            case ResponseType.NOT_IMPLEMENTED =>
              feedback("购买失败,经济插件未加载,请联系服务器管理员")
              logger.error("经济插件未加载")
            case null =>
              feedback("未知错误")
              logger.error("未知错误")
          }

        } catch case e: Throwable => {
          feedback(s"购买失败[${Option(e.getMessage).getOrElse("未知错误")}]")
          feedback("请联截图系管理员处理")
        }
      }
    } else {
      //没有足够的货币
      feedback(comp"购买[$energy]能量所需的${currency}不足,当前拥有的${currency}[$totalMoney]")
    }

  }

  def exchange(player: Player)(using context: CommandContext[CommandSourceStack]): Unit = {
    val key = getArg[String]("money_item")
    val count = getArgOption[Int]("count").getOrElse(1)
    var countTemp = count

    getMoneyItemEnergy(key).foreach { case (item, energy) =>
      val inventory = player.getInventory

      var actions: List[() => Int] = Nil

      Option(inventory.getContents).get
        .filter { stack =>
          if (stack != null && !stack.isEmpty) {
            item.isMatch(stack)
          } else false
        }.foreach { stack =>
          val temp = countTemp.min(stack.getAmount)
          countTemp -= temp
          actions = actions.:: { () =>
            stack.setAmount(stack.getAmount - temp)
            temp
          }
        }
      //是否有充足的货币
      if (countTemp == 0) {
        val totalEnergy = energy * count
        if (totalEnergy + player.energy > maxEnergy) {
          //超出了上限
          feedback(comp"超出了能量上限，最大值:[$maxEnergy],充值后会超出上限:[${energy + player.energy - maxEnergy}]")
        } else {
          var consumeCount = 0
          try {
            actions.foreach { action =>
              consumeCount += action()
            }
          } catch case e: Throwable => {
            feedback(s"购买失败[${Option(e.getMessage).getOrElse("未知错误")}]")
            feedback("请联截图系管理员处理")
          }
          if (consumeCount > 0) {
            Future {
              player.updateEnergy((player.energy + consumeCount * energy).max(0).min(maxEnergy))
            }.onComplete {
              case Success(value) =>
                feedback(comp"购买成功[${item.getItemStack(consumeCount).get} -> ${consumeCount * energy}]的能量,当前剩余能量值[${player.energy}]")
              case Failure(exception) =>
                feedback(s"购买失败,请联截图系管理员处理")
                logger.error("购买飞行能量失败,可能是数据库问题", exception)
            }
          }
        }

      } else {
        feedback(s"你所拥有的对应货币[$key]数量不足")
        feedback(s"需要[$count]个,在背包中找到[${count - countTemp}]个")
      }
    }
  }

  def getMoneyItemEnergy(id: String): Option[(SpecialItem, Double)] = {
    SpecialItemManager.specifyType(moneyItem.keys.toSet).get(id) match {
      case Some(value) => Some(value -> moneyItem.get(id).get)
      case None => None
    }
  }

}
