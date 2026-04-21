package cn.bakamc.folia.command

import cn.bakamc.folia.command.dsl.ArgumentScope.*
import cn.bakamc.folia.command.dsl.ContextOps.{getPlayer, getPlayers}
import cn.bakamc.folia.command.dsl.RootCommand
import cn.bakamc.folia.functional.flightenergy.{FlightEnergyCommandOps, FlightEnergyManager}
import com.mojang.brigadier.arguments.{BoolArgumentType, DoubleArgumentType}
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import org.bukkit.entity.Player

import scala.jdk.CollectionConverters.*


object FlightEnergyCommand {

  private def manager: FlightEnergyManager.type = FlightEnergyManager

  private def ops: FlightEnergyCommandOps.type = FlightEnergyCommandOps

  val root: RootCommand = RootCommand("fly") {
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

  }


}
