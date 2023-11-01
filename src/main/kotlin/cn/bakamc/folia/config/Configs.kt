package cn.bakamc.folia.config

import cn.bakamc.folia.config.base.ConfigBlockInfos
import cn.bakamc.folia.config.base.ConfigEntityInfos
import cn.bakamc.folia.config.base.ConfigStringDoubleMap
import cn.bakamc.folia.config.base.ConfigStringListMap
import cn.bakamc.folia.event.entity.BlockInfo
import cn.bakamc.folia.event.entity.EntityInfo
import cn.bakamc.folia.util.logger
import moe.forpleuvoir.nebula.config.category.ConfigCategoryImpl
import moe.forpleuvoir.nebula.config.item.impl.ConfigDouble
import moe.forpleuvoir.nebula.config.item.impl.ConfigEnum
import moe.forpleuvoir.nebula.config.item.impl.ConfigString
import moe.forpleuvoir.nebula.config.item.impl.ConfigTime
import moe.forpleuvoir.nebula.config.manager.LocalConfigManager
import moe.forpleuvoir.nebula.config.persistence.JsonConfigManagerPersistence
import moe.forpleuvoir.nebula.config.util.ConfigUtil
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import java.nio.file.Path
import kotlin.time.DurationUnit

object Configs : LocalConfigManager("bakamc"), JsonConfigManagerPersistence {

    override lateinit var configPath: Path
        internal set

    /**
     * 配置文件路径
     * @param path Path
     */
    suspend fun init(path: Path) {
        configPath = path
        backup()
        init()
        generateTemp()
        runCatching {
            load()
            if (this.needSave) {
                save()
            }
        }.onFailure {
            it.printStackTrace()
            forceSave()
        }
    }

    @Suppress("RedundantSuspendModifier")
    internal suspend fun backup() {
        runCatching {
            ConfigUtil.run {
                val backupFile = configFile(fileName("${key}.backup"), configPath)
                val file = configFile(fileName(key), configPath)
                file.copyTo(backupFile, true)
            }
        }.onFailure {
            logger.error("备份配置文件失败", it)
        }.onSuccess {
            logger.info("备份配置文件成功")
        }

    }

    /**
     * 生成当前版本的默认配置文件
     */
    @Suppress("RedundantSuspendModifier")
    internal suspend fun generateTemp() {
        runCatching {
            ConfigUtil.run {
                val file = configFile(fileName("${key}.temp"), configPath)
                writeStringToFile(serializeObjectToString(serialization().asObject), file)
            }
        }.onFailure {
            logger.error("模板文件生成失败", it)
        }.onSuccess {
            logger.info("模板文件生成成功")
        }
    }

    object Database : ConfigCategoryImpl("database") {

        val URL by ConfigString("url", "jdbc:mysql://localhost:3306/bakamc?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai")

        val USER by ConfigString("user", "root")

        val PASSWORD by ConfigString("password", "root")

    }

    object FlightEnergy : ConfigCategoryImpl("flight_energy") {

        val TICK_PERIOD by ConfigTime("tick_period", 1.0, DurationUnit.SECONDS)

        val ENERGY_COST by ConfigDouble("energy_cost", 1.0)

        val MAX_ENERGY by ConfigDouble("max_energy", 5000.0)

        val SYNC_PERIOD by ConfigTime("sync_period", 5.0, DurationUnit.MINUTES)

        object EnergyBar : ConfigCategoryImpl("energy_bar") {

            val COLOR: BarColor by ConfigEnum("color", BarColor.GREEN)

            val TITLE by ConfigString("title", "飞行能量: %.2f(%+.2f)/%.2f")

            val STYLE: BarStyle by ConfigEnum("style", BarStyle.SEGMENTED_10)

        }

        val MONEY_ITEM by ConfigStringDoubleMap(
            "money_item",
            mapOf(
                "⑨币" to 5000.0,
                "冰辉石" to 78.125
            )
        )

    }

    object Entity : ConfigCategoryImpl("entity") {

        val ENTITY_INFOS by ConfigEntityInfos(
            "entity_infos",
            mapOf(
                "小黑" to EntityInfo(type = "minecraft:enderman"),
                "苦力怕" to EntityInfo(type = "minecraft:creeper")
            )
        )

        object ChangeBlock : ConfigCategoryImpl("change_block") {

            val BLOCK_INFOS by ConfigBlockInfos(
                "block_infos",
                mapOf(
                    "西瓜" to BlockInfo(type = "minecraft:melon_block"),
                    "主世界方块" to BlockInfo(world = "minecraft:overworld"),
                )
            )

            val ENTITY_MAP by ConfigStringListMap(
                "entity_map",
                mapOf(
                    "小黑" to listOf("主世界方块 -> minecraft:air"),
                    "苦力怕" to listOf("主世界方块 -> minecraft:air")
                )
            )


        }


    }

}