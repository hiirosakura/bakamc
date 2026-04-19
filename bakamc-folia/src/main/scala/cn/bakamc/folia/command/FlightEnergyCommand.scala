package cn.bakamc.folia.command

import cn.bakamc.common.comp
import cn.bakamc.folia.command.dsl.ArgumentScope.*
import cn.bakamc.folia.command.dsl.RootCommand
import cn.bakamc.folia.functional.flightenergy.FlightEnergyManager
import cn.bakamc.folia.util.text.PluginComponentAdapter.given_ComponentAdapter
import com.mojang.brigadier.arguments.{BoolArgumentType, DoubleArgumentType}
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import org.bukkit.entity.Player

import scala.jdk.CollectionConverters.*


object FlightEnergyCommand {

  private def manager: FlightEnergyManager.type = FlightEnergyManager

  val root: RootCommand = RootCommand("fly") {
    //直接执行切换飞行开关
    execute {
      manager.toggleFly(senderPlayer, None)
    }

    //info 指令
    "info" {
      requires("bakamc.fly.info")
      argument("player", ArgumentTypes.player()) {
        execute {
          val player = getArg[Player]("player")
          val (status, energy) = manager.getInfo(player)
          feedback(comp"$player 飞行状态[$status],当前飞行能量[$energy]")
        }
      }
    }

    //切换飞行开关
    "enable" {
      execute {
        manager.toggleFly(senderPlayer, None)
      }
      argument("status", BoolArgumentType.bool()) {
        execute {
          manager.toggleFly(senderPlayer, Some(getArg[Boolean]("status")))
        }
      }
    }

    //飞行能量条
    "bar" {
      execute {
        manager.toggleBarVisible(senderPlayer, None)
      }
      argument("status", BoolArgumentType.bool()) {
        execute {
          manager.toggleBarVisible(senderPlayer, Some(getArg[Boolean]("status")))
        }
      }
    }

    //设置
    "set" {
      requires("bakamc.fly.set")
      argument("player", ArgumentTypes.players()) {
        argument("energy", DoubleArgumentType.doubleArg()) {
          execute {
            val players = getArg[java.util.List[Player]]("player").asScala
            val energy = getArg[Double]("energy")


          }
        }
      }
    }

  }


}
