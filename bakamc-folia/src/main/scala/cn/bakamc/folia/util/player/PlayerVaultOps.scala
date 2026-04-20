package cn.bakamc.folia.util.player

import cn.bakamc.folia.BakaMC
import cn.bakamc.folia.hook.VaultUnlocked
import net.milkbowl.vault2.economy.{Economy, EconomyResponse}
import org.bukkit.entity.Player

object PlayerVaultOps {

  private def economy: Economy = VaultUnlocked.economy

  given Conversion[Double, BigDecimal] = BigDecimal(_)

  extension (player: Player) {

    def money(currency: Option[String], world: String = player.getWorld.getName): BigDecimal = {
      try {
        currency match {
          case Some(currency) =>
            economy.balance(BakaMC.name, player.getUniqueId, world, currency)
          case None =>
            economy.balance(BakaMC.name, player.getUniqueId, world)
        }
      } catch {
        case e: Throwable =>
          BakaMC.logger.error("经济插件未加载", e)
          throw e
      }
    }

    def withdraw(money: BigDecimal, currency: Option[String], world: String = player.getWorld.getName): EconomyResponse = {
      try {
        currency match {
          case Some(currency) =>
            economy.withdraw(BakaMC.name, player.getUniqueId, world, currency, money.bigDecimal)
          case None =>
            economy.withdraw(BakaMC.name, player.getUniqueId, world, money.bigDecimal)
        }
      } catch {
        case e: Throwable =>
          BakaMC.logger.error("经济插件未加载", e)
          throw e
      }
    }

    def deposit(money: BigDecimal, currency: Option[String], world: String = player.getWorld.getName): EconomyResponse = {
      try {
        currency match {
          case Some(currency) =>
            economy.deposit(BakaMC.name, player.getUniqueId, world, currency, money.bigDecimal)
          case None =>
            economy.deposit(BakaMC.name, player.getUniqueId, world, money.bigDecimal)
        }
      } catch {
        case e: Throwable =>
          BakaMC.logger.error("经济插件未加载", e)
          throw e
      }
    }

  }

}
