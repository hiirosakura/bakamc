package cn.bakamc.common.config

import moe.forpleuvoir.nebula.common.util.plus
import moe.forpleuvoir.nebula.common.util.second
import moe.forpleuvoir.nebula.config.category.ConfigCategoryImpl
import moe.forpleuvoir.nebula.config.manager.AutoSaveConfigManager
import moe.forpleuvoir.nebula.config.manager.LocalConfigManager
import moe.forpleuvoir.nebula.config.persistence.JsonConfigManagerPersistence
import java.nio.file.Path
import java.util.*

object Configs : LocalConfigManager("bakamc"), AutoSaveConfigManager, JsonConfigManagerPersistence {

    override lateinit var configPath: Path
        private set

    override val starTime: Date = Date() + 30.second
    override val period: Long = 30.second

    override fun init() {
        super<LocalConfigManager>.init()
        super<AutoSaveConfigManager>.init()
    }

    /**
     * 配置文件路径
     * @param path Path
     */
    fun init(path: Path) {
        configPath = path
        init()
    }

    object FlightEnergy: ConfigCategoryImpl("flight_energy"){




    }

}