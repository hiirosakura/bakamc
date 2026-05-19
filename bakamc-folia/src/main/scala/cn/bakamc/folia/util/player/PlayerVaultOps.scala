package cn.bakamc.folia.util.player

import cn.bakamc.folia.BakaMC
import cn.bakamc.folia.hook.{BakaEconomy, VaultUnlocked}
import net.milkbowl.vault2.economy.{Economy, EconomyResponse}
import org.bukkit.entity.Player

import language.implicitConversions

object PlayerVaultOps {

  inline private def economy: BakaEconomy = VaultUnlocked.economy

  given Conversion[Double, BigDecimal] = BigDecimal(_)

  extension (player: Player) {

    def money(currency: Option[String], world: String = player.getWorld.getName): BigDecimal = {
      try {
        economy.balance(player, world, currency)
      } catch {
        case e: Throwable =>
          BakaMC.logger.error("经济插件未加载", e)
          throw e
      }
    }

    def withdraw(money: BigDecimal, currency: Option[String], world: String = player.getWorld.getName): EconomyResponse = {
      try {
        economy.withdraw(money.bigDecimal, player, world, currency)
      } catch {
        case e: Throwable =>
          BakaMC.logger.error("经济插件未加载", e)
          throw e
      }
    }

    def deposit(money: BigDecimal, currency: Option[String], world: String = player.getWorld.getName): EconomyResponse = {
      try {
        economy.deposit(money.bigDecimal, player, world, currency)
      } catch {
        case e: Throwable =>
          BakaMC.logger.error("经济插件未加载", e)
          throw e
      }
    }

  }

}
