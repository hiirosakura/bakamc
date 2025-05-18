package cn.bakamc.folia.config

import moe.forpleuvoir.nebula.config.container.ConfigContainerImpl
import moe.forpleuvoir.nebula.config.item.impl.*
import net.kyori.adventure.bossbar.BossBar
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object FlightEnergyConfig : ConfigContainerImpl("flight_energy") {

    val TICK_PERIOD by duration("tick_period", 1.0.seconds)

    val ENERGY_COST by double("energy_cost", 1.0)

    val MAX_ENERGY by double("max_energy", 5000.0)

    val SYNC_PERIOD by duration("sync_period", 5.0.minutes)

    val ALLOW_FLY_WORLD by stringList("allow_fly_world", listOf("world"))

    val FORBID_FLY_WORLD_MESSAGE by string("forbid_fly_world_message", "&{#FF0000}所在的世界禁止飞行")

    val CLOSE_ADVENTURE_PLAYERS_FLYING by boolean("close_adventure_players_flying", false)

    val ENERGY_PRICE_MAP by stringDoubleMap(
        "energy_price", mapOf(
            "coin_1" to 1.0
        )
    )

    val ONLINE_DURATION_DISCOUNT_MAP by stringDoubleMap(
        "online_duration_discount", mapOf(
            "10h" to 0.95,
            "1d" to 0.90,
            "7d" to 0.7,
            "30d" to 0.6,
        )
    )

    fun onlineDiscount(onlineDuration: Duration): Double {
        buildMap {
            ONLINE_DURATION_DISCOUNT_MAP.forEach { (key, value) ->
                runCatching {
                    this[Duration.parse(key)] = value.coerceIn(0.0, 1.0)
                }
            }
        }.toSortedMap { o1, o2 ->
            o2.compareTo(o1)
        }.forEach { (duration, value) ->
            if (onlineDuration >= duration) {
                return value
            }
        }
        return 1.0
    }

    val MONEY_ITEM by stringDoubleMap(
        "money_item",
        mapOf(
            "⑨币" to 5000.0,
            "冰辉石" to 78.125
        )
    )

    private val energyBar = addConfig(EnergyBar)

    object EnergyBar : ConfigContainerImpl("energy_bar") {

        val COLOR: BossBar.Color by enum("color", BossBar.Color.GREEN)

        val TITLE by string("title", "飞行能量: %.2f(%+.2f)/%.2f")

        val STYLE: BossBar.Overlay by enum("style", BossBar.Overlay.NOTCHED_10)

    }

}