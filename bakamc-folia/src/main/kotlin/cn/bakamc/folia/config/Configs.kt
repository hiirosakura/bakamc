package cn.bakamc.folia.config

import moe.forpleuvoir.nebula.config.category.ConfigCategoryImpl
import moe.forpleuvoir.nebula.config.item.impl.ConfigBoolean
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
    }

    object Database: ConfigCategoryImpl("database") {

        val URL by ConfigString("url", "jdbc:mysql://localhost:3306/bakamc?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai")

        val USER by ConfigString("user", "root")

        val PASSWORD by ConfigString("password", "root")

    }

    object FlightEnergy: ConfigCategoryImpl("flight_energy"){




    }


    object Entity: ConfigCategoryImpl("entity") {
        val ENDERMAN_CAN_BREAK_BLOCK by ConfigBoolean("enderman_can_break_block", true)
    }

}