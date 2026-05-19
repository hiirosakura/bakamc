package cn.bakamc.folia.config

import moe.forpleuvoir.nebula.config.Comment

object Configs extends PluginConfigManager("config") {

  @Comment("杂项")
  val misc: MiscConfig.type = MiscConfig


  @Comment("飞行能量")
  val flightEnergy: FlightEnergyConfig.type = FlightEnergyConfig

  @Comment("实体相关配置")
  val entity: EntityConfig.type = EntityConfig



}
