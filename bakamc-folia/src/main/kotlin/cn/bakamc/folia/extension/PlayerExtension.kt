package cn.bakamc.folia.extension

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.db.table.PlayerInfo
import cn.bakamc.folia.hook.VaultUnlocked
import cn.bakamc.folia.util.logger
import net.milkbowl.vault2.economy.EconomyResponse
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import org.bukkit.Statistic.PLAY_ONE_MINUTE
import org.bukkit.entity.Player
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

val Player.info: PlayerInfo
    get() {
        return PlayerInfo {
            uuid = this@info.uuid
            name = this@info.name
        }
    }

val Player.uuid: String
    get() = this.uniqueId.toString()

val onlinePlayers: Collection<Player> get() = BakaMCPlugin.instance.server.onlinePlayers

fun Player.toServerPlayer() = MinecraftServer.getServer().playerList.getPlayer(this.uniqueId)

fun ServerPlayer.toPluginPlayer() = BakaMCPlugin.instance.server.getPlayer(this.uuid)

fun Player.onlineDuration(): Duration {
    return (this.getStatistic(PLAY_ONE_MINUTE) / 20).toDuration(DurationUnit.SECONDS)
}

fun Player.money(currency: String? = null, world: String = this.world.name): Double {
    return runCatching {
        if (currency != null)
            VaultUnlocked.economy!!.balance(BakaMCPlugin.instance.name, this.uniqueId, world, currency).toDouble()
        else
            VaultUnlocked.economy!!.balance(BakaMCPlugin.instance.name, this.uniqueId, world).toDouble()
    }.onFailure {
        logger.error("经济插件未加载!", it)
    }.getOrThrow()
}

/**
 * 从玩家的账户中取出指定金额。
 *
 * @param money 要取出的金额
 * @return EconomyResponse 表示取款操作的结果，包括成功或失败状态以及关联信息
 */
fun Player.withdraw(money: Double, currency: String? = null, world: String = this.world.name): EconomyResponse {
    return runCatching {
        if (currency != null)
            VaultUnlocked.economy!!.withdraw(BakaMCPlugin.instance.name, this.uniqueId, world, currency, money.toBigDecimal())
        else
            VaultUnlocked.economy!!.withdraw(BakaMCPlugin.instance.name, this.uniqueId, world, money.toBigDecimal())
    }.onFailure {
        logger.error("经济插件未加载!", it)
    }.getOrThrow()
}

/**
 * 为当前玩家的经济账户存入指定金额。
 *
 * @param money 存入的金额，类型为 BigDecimal。
 * @return EconomyResponse 对象，表示存款操作的结果。
 */
fun Player.deposit(money: Double, currency: String? = null, world: String = this.world.name): EconomyResponse {
    return runCatching {
        if (currency != null)
            VaultUnlocked.economy!!.deposit(BakaMCPlugin.instance.name, this.uniqueId, world, currency, money.toBigDecimal())
        else
            VaultUnlocked.economy!!.deposit(BakaMCPlugin.instance.name, this.uniqueId, world, money.toBigDecimal())
    }.onFailure {
        logger.error("经济插件未加载!", it)
    }.getOrThrow()
}


fun ServerPlayer.money(currency: String? = null, world: String): Double {
    return runCatching {
        if (currency != null)
            VaultUnlocked.economy!!.balance(BakaMCPlugin.instance.name, this.uuid, world, currency).toDouble()
        else
            VaultUnlocked.economy!!.balance(BakaMCPlugin.instance.name, this.uuid, world).toDouble()
    }.onFailure {
        logger.error("经济插件未加载!", it)
    }.getOrThrow()
}
