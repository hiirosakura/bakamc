package cn.bakamc.folia.config

import cn.bakamc.folia.config.base.ConfigStringDoubleMap
import moe.forpleuvoir.nebula.common.util.minute
import moe.forpleuvoir.nebula.common.util.second
import moe.forpleuvoir.nebula.config.category.ConfigCategoryImpl
import moe.forpleuvoir.nebula.config.item.impl.*
import moe.forpleuvoir.nebula.config.manager.LocalConfigManager
import moe.forpleuvoir.nebula.config.persistence.JsonConfigManagerPersistence
import java.nio.file.Path

object Configs : LocalConfigManager("bakamc"), JsonConfigManagerPersistence {

    override lateinit var configPath: Path
        private set

    /**
     * 配置文件路径
     * @param path Path
     */
    fun init(path: Path) {
        configPath = path
        init()
        runCatching {
            load()
        }.onFailure {
            it.printStackTrace()
            forceSave()
        }
    }

    object Database : ConfigCategoryImpl("database") {

        val URL by ConfigString("url", "jdbc:mysql://localhost:3306/bakamc?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai")

        val USER by ConfigString("user", "root")

        val PASSWORD by ConfigString("password", "root")

    }

    object FlightEnergy : ConfigCategoryImpl("flight_energy") {

        val TICK_PERIOD by ConfigLong("tick_period", 1.second)

        val ENERGY_COST by ConfigDouble("energy_cost", 1.0)

        val MAX_COST by ConfigDouble("max_cost", 5000.0)

        val SYNC_PERIOD by ConfigLong("sync_period", 5.minute)

        val MONEY_ITEM by ConfigStringDoubleMap(
            "money_item",
            mapOf(
                "⑨币" to 5000.0,
                "冰辉石" to 78.125
            )
        )

    }

    object Entity : ConfigCategoryImpl("entity") {
        val DISABLE_ENDERMAN_BREAK_BLOCK by ConfigBoolean("disable_enderman_break_block", true)
    }

}