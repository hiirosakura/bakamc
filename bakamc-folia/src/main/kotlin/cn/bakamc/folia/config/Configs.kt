package cn.bakamc.folia.config

import moe.forpleuvoir.nebula.common.util.minute
import moe.forpleuvoir.nebula.config.category.ConfigCategoryImpl
import moe.forpleuvoir.nebula.config.item.impl.ConfigBoolean
import moe.forpleuvoir.nebula.config.item.impl.ConfigDouble
import moe.forpleuvoir.nebula.config.item.impl.ConfigInt
import moe.forpleuvoir.nebula.config.item.impl.ConfigString
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

        val ENERGY_COST by ConfigDouble("energy_cost", 0.001)

        val SYNC_PERIOD by ConfigInt("sync_period", 5.minute.toInt())


    }


    object Entity : ConfigCategoryImpl("entity") {
        val ENDERMAN_CAN_BREAK_BLOCK by ConfigBoolean("enderman_can_break_block", true)
    }

}