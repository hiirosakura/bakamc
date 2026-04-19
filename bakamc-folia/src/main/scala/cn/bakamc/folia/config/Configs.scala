package cn.bakamc.folia.config

import moe.forpleuvoir.nebula.config.Comment

object Configs extends PluginConfigManager("config") {

  @Comment(text = "飞行能量")
  val flightEnergy: FlightEnergyConfig.type = FlightEnergyConfig

}
