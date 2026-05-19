package cn.bakamc.folia.hook

import cn.bakamc.folia.BakaMC
import net.milkbowl.vault.economy.EconomyResponse.ResponseType
import net.milkbowl.vault.economy.{Economy as EconomyV1, EconomyResponse as EconomyResponseV1}
import net.milkbowl.vault2.economy.{Economy, EconomyResponse}
import org.bukkit.entity.Player

import scala.language.implicitConversions

object VaultUnlocked extends BakaMCHook {

  private var _economy: Option[BakaEconomy] = None

  def economy: BakaEconomy = _economy.get

  override def onEnable(plugin: BakaMC): Unit = {
    val vault = plugin.getServer.getPluginManager.getPlugin("Vault")
    if (vault == null) {
      plugin.logger.warn("未找到Vault插件")
      return
    }
    setupEconomy(plugin)
  }


  private def setupEconomy(plugin: BakaMC): Unit = {
    val rsp = plugin.getServer.getServicesManager.getRegistration(classOf[Economy])
    val rspV1 = plugin.getServer.getServicesManager.getRegistration(classOf[EconomyV1])
    if (rsp != null) {
      _economy = Option(BakaEconomy(rsp.getProvider))
      return
    }
    if (rspV1 != null) {
      _economy = Option(BakaEconomy(rspV1.getProvider))
    }
  }

  override def onDisable(plugin: BakaMC): Unit = {
    _economy = None
  }

}

private given Conversion[EconomyResponseV1, EconomyResponse] = { v1 =>
  EconomyResponse(
    BigDecimal.valueOf(v1.amount).bigDecimal,
    BigDecimal.valueOf(v1.balance).bigDecimal,
    v1.`type` match {
      case ResponseType.SUCCESS => EconomyResponse.ResponseType.SUCCESS
      case ResponseType.FAILURE => EconomyResponse.ResponseType.FAILURE
      case ResponseType.NOT_IMPLEMENTED => EconomyResponse.ResponseType.NOT_IMPLEMENTED
    },
    v1.errorMessage
  )
}

class BakaEconomy(private val economy: Economy | EconomyV1) {

  private def pluginName = BakaMC.name

  def isV1: Boolean = economy.isInstanceOf[EconomyV1]

  def balance(player: Player, world: String, currency: String | Option[String]): BigDecimal = economy match {
    case e: Economy => currency match {
      case s: String => e.balance(pluginName, player.getUniqueId, world, s)
      case Some(s) => e.balance(pluginName, player.getUniqueId, world, s)
      case None => e.balance(pluginName, player.getUniqueId, world)
    }
    case e: EconomyV1 => e.getBalance(player, world)
  }


  def withdraw(money: BigDecimal, player: Player, world: String, currency: String | Option[String]): EconomyResponse = economy match {
    case e: Economy => currency match {
      case s: String => e.withdraw(pluginName, player.getUniqueId, world, s, money.bigDecimal)
      case Some(s) => e.withdraw(pluginName, player.getUniqueId, world, s, money.bigDecimal)
      case None => e.withdraw(pluginName, player.getUniqueId, world, money.bigDecimal)
    }
    case v1: EconomyV1 => v1.withdrawPlayer(player, money.doubleValue)
  }

  def deposit(money: BigDecimal, player: Player, world: String, currency: String | Option[String]): EconomyResponse = economy match {
    case e: Economy => currency match {
      case s: String => e.deposit(pluginName, player.getUniqueId, world, s, money.bigDecimal)
      case Some(s) => e.deposit(pluginName, player.getUniqueId, world, s, money.bigDecimal)
      case None => e.deposit(pluginName, player.getUniqueId, world, money.bigDecimal)
    }
    case v1: EconomyV1 => v1.depositPlayer(player, money.doubleValue)
  }


}
