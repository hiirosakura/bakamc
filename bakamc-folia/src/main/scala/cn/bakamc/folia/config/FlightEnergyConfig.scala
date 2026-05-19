package cn.bakamc.folia.config

import cn.bakamc.common.inlinestyletext.InlineStyleTextParser
import moe.forpleuvoir.nebula.config.item.*
import moe.forpleuvoir.nebula.config.{Comment, CommentLines, ConfigGroup}
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component

import scala.concurrent.duration.{Duration, DurationInt, FiniteDuration}
import scala.language.implicitConversions

object FlightEnergyConfig extends ConfigGroup("flight_energy") {

  @Comment("数据库同步周期,每隔一段时间将数据同步至数据库")
  val syncPeriod = ConfigFiniteDuration("sync_period", 5.minute)

  @Comment("飞行能量计算周期")
  val tickPeriod = ConfigFiniteDuration("tick_period", 1.second)

  @Comment("每次计算时消耗的飞行能量基数")
  val energyCost = ConfigDouble("energy_cost", 1)

  @Comment("玩家可以拥有的最大飞行能量")
  val maxEnergy = ConfigDouble("max_energy", 5000)

  @Comment("允许飞行的世界")
  val allowFlyWorld = ConfigList[String]("allow_fly_world", List("world"))

  @Comment("禁止飞行世界提示")
  private val forbidFlyWorldMessage = ConfigString("forbid_fly_world_message", "&{#FF0000}所在的世界禁止飞行")

  private var _forbidFlyWorldMessageComponent: Component = InlineStyleTextParser.DEFAULT.parse(forbidFlyWorldMessage)

  def forbidFlyWorldMessageComponent: Component = _forbidFlyWorldMessageComponent

  forbidFlyWorldMessage.observe { e =>
    _forbidFlyWorldMessageComponent = InlineStyleTextParser.DEFAULT.parse(e)
  }

  @Comment("是否禁止冒险模式下的飞行")
  val disableAdventureModeFly = ConfigBoolean("disable_adventure_mode_fly", false)

  @CommentLines(Array(
    "飞行能量价格映射,每1货币对应的飞行能量",
    "如果经济插件不支持Vault的多货币将会使用#default的值,#default未设置时默认为1"
  ))
  val energyPriceMap = ConfigMap[Double]("energy_price_map", Map(
    "#default" -> 1.0,
    "coin_1" -> 1.0
  ))

  def currencies: List[String] = energyPriceMap.keys.map(s => s"\"$s\"").toList

  def energyPrice(currency: String): BigDecimal =
    BigDecimal.valueOf(energyPriceMap.get(currency).getOrElse(energyPriceMap.get("#default").getOrElse(1.0)))

  @Comment("在线时长折扣映射,在线时长达到对应时间时获得相应的折扣")
  private val _onlineTimeDiscountMap = ConfigMap[Double]("online_time_discount_map", Map(
    10.hour.toString -> 0.95,
    1.day.toString -> 0.9,
    7.day.toString -> 0.7,
    30.day.toString -> 0.6
  ))

  def onlineTimeDiscountMap: Map[FiniteDuration, Double] = _onlineTimeDiscountMap.getValue.map {
    case (str, d) => (Duration(str).asInstanceOf[FiniteDuration], d.max(0.1).min(1.0))
  }

  def onlineDiscount(onlineDuration: FiniteDuration): Double = {
    val discountMap = onlineTimeDiscountMap
    discountMap.keys.foldLeft(Option.empty[FiniteDuration]) {
      case (None, d) if d < onlineDuration => Some(d)
      case (Some(max), d) if d < onlineDuration && d > max => Some(d)
      case (acc, _) => acc
    }.map(discountMap.apply).getOrElse(1.0)
  }

  @Comment("货币物品映射,物品名称对应物品价格")
  val moneyItem = ConfigMap[Double]("moneyItem", Map(
    "⑨币" -> 5000,
    "冰辉石" -> 78.125
  ))

  object EnergyBar extends ConfigGroup("energy_bar") {

    @Comment("能量条颜色[PINK ,BLUE ,RED ,GREEN ,YELLOW ,PURPLE, WHITE]")
    val color = ConfigJavaEnum[BossBar.Color]("color", BossBar.Color.GREEN)

    @Comment("能量条标题")
    val title = ConfigString("title", "飞行能量: %.2f(%+.2f)/%.2f")

    @Comment("能量条样式[PROGRESS, NOTCHED6, NOTCHED10, NOTCHED12, NOTCHED20]")
    val style = ConfigJavaEnum[BossBar.Overlay]("style", BossBar.Overlay.NOTCHED_10)

  }

}
