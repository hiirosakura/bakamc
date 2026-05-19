package cn.bakamc.folia.command

import cn.bakamc.common.comp
import cn.bakamc.folia.command.dsl.ArgumentScope.*
import cn.bakamc.folia.command.dsl.ContextOps.{getPlayer, getPlayers}
import cn.bakamc.folia.command.dsl.RootCommand
import cn.bakamc.folia.config.FlightEnergyConfig
import cn.bakamc.folia.functional.flightenergy.FlightEnergyPlayerOps.{energy, onlineDuration}
import cn.bakamc.folia.functional.flightenergy.{FlightEnergyCommandOps, FlightEnergyManager}
import cn.bakamc.folia.functional.specialitem.SpecialItemManager
import cn.bakamc.folia.hook.VaultUnlocked
import cn.bakamc.folia.util.player.PlayerVaultOps.money
import cn.bakamc.folia.util.text.PluginComponentAdapter.given
import com.mojang.brigadier.arguments.{BoolArgumentType, DoubleArgumentType, IntegerArgumentType, StringArgumentType}
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import org.bukkit.entity.Player

import scala.jdk.CollectionConverters.*
import scala.language.implicitConversions


object FlightEnergyCommand {

  private def manager: FlightEnergyManager.type = FlightEnergyManager

  private def ops: FlightEnergyCommandOps.type = FlightEnergyCommandOps

  private def config: FlightEnergyConfig.type = FlightEnergyConfig

  val root: RootCommand = RootCommand("fly") {
    requires("bakamc.fly")
    //直接执行切换飞行开关
    execute {
      ops.toggleFly(senderPlayer, None)
    }

    //info 指令
    "info" {
      execute {
        ops.info(senderPlayer)
      }
      argument("player", ArgumentTypes.player()) {
        requires("bakamc.fly.info")
        execute {
          ops.info(getPlayer("player"))
        }
      }
    }

    //切换飞行开关
    "enable" {
      execute {
        ops.toggleFly(senderPlayer, None)
      }
      argument("status", BoolArgumentType.bool()) {
        execute {
          ops.toggleFly(senderPlayer, Some(getArg[Boolean]("status")))
        }
      }
    }

    //飞行能量条
    "bar" {
      execute {
        ops.toggleBarVisible(senderPlayer, None)
      }
      argument("status", BoolArgumentType.bool()) {
        execute {
          ops.toggleBarVisible(senderPlayer, Some(getArg[Boolean]("status")))
        }
      }
    }

    //设置飞行能量
    "set" {
      requires("bakamc.fly.set")
      argument("player", ArgumentTypes.players()) {
        argument("energy", DoubleArgumentType.doubleArg()) {
          execute {
            val players = getPlayers("player")
            val energy = getArg[Double]("energy")
            ops.updateOnlinePlayerEnergy(sender, players, false) { (currentEnergy, setter) =>
              setter(energy)
            }
          }
        }
      }
    }

    //添加飞行能量
    "add" {
      requires("bakamc.fly.add")
      argument("player", ArgumentTypes.players()) {
        argument("energy", DoubleArgumentType.doubleArg()) {
          execute {
            val players = getPlayers("player")
            val energy = getArg[Double]("energy")
            ops.updateOnlinePlayerEnergy(sender, players, true) { (currentEnergy, setter) =>
              setter(currentEnergy + energy)
            }
          }
        }
      }
    }

    //充值
    "recharge" {
      argument("currency", StringArgumentType.string()) {
        suggestsBuild {
          if (VaultUnlocked.economy.isV1) {
            List("\"#default\"")
          } else config.currencies
        }
        argument("energy", DoubleArgumentType.doubleArg(0.0)) {
          suggestsBuild {
            val currency = getArg[String]("currency")
            val price = config.energyPrice(currency)
            val energy = summon[SuggestionsBuilder].getRemaining.toDoubleOption.getOrElse(0.0)
            val money = senderPlayer.money(Some(currency))
            val discount = config.onlineDiscount(senderPlayer.onlineDuration)
            val cost = price * energy * discount
            List(
              s"当前飞行能量购买所需货币§a[$cost]§r(当前在线时长折扣§a[$discount])",
              s"当前持有货币§a[${money}]§r,购买后剩余[${money - cost}]",
              s"输入需要购买的飞行能量数值,每1§a[$currency]§r可以购买§a[${1 / price}]§r飞行能量" -> comp"当前飞行能量[${senderPlayer.energy}/${config.maxEnergy}]",
            )
          }
          execute {
            ops.recharge(senderPlayer)
          }
        }
      }
    }

    //使用特殊物品交换
    "exchange" {
      argument("money_item", StringArgumentType.string()) {
        suggests {
          SpecialItemManager.specifyType(config.moneyItem.keySet.toSet).keys.map(s => s"\"$s\"").toList
        }
        execute {
          ops.exchange(senderPlayer)
        }

        argument("count", IntegerArgumentType.integer(0)) {
          suggestsBuild {
            val itemKey = getArg[String]("money_item")
            val energy = config.moneyItem.get(itemKey)
            val count = summon[SuggestionsBuilder].getRemaining.toIntOption.getOrElse(0)
            energy match {
              case Some(v) =>
                List(
                  s"每个§a[${itemKey}]§r可以兑换§a[$v]§r飞行能量,当前可兑换§a[${count * v}]§r飞行能量" -> comp"当前飞行能量[${senderPlayer.energy}/${config.maxEnergy}]"
                )
              case None =>
                List(s"§c无效的货币[$itemKey]类型!")
            }
          }
          execute {
            ops.exchange(senderPlayer)
          }
        }


      }


    }

  }


}
