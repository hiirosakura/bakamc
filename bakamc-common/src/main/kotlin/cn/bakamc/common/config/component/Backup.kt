package cn.bakamc.common.config.component

import kotlinx.coroutines.runBlocking
import moe.forpleuvoir.nebula.config.manager.ConfigManager
import moe.forpleuvoir.nebula.config.manager.ConfigManagerComponentScope
import moe.forpleuvoir.nebula.config.manager.component.ConfigManagerComponent
import moe.forpleuvoir.nebula.config.persistence.JsonConfigManagerPersistence.wrapFileName
import moe.forpleuvoir.nebula.config.util.ConfigUtil
import org.slf4j.Logger
import java.nio.file.Path

class Backup(
    val manager: () -> ConfigManager,
    val configPath: () -> Path,
    val logger: () -> Logger
) : ConfigManagerComponent {

    override fun beginInit(): Unit = runBlocking {
        runCatching {
            ConfigUtil.run {
                val fileName = wrapFileName(manager().key)
                val backupFile = configFile("$fileName.backup", configPath())
                val file = configFile(fileName, configPath())
                file.copyTo(backupFile, true)
            }
        }.onFailure {
            logger().error("备份配置文件失败", it)
        }.onSuccess {
            logger().info("备份配置文件成功")
        }
    }

}

fun ConfigManager.backup(
    configPath: () -> Path,
    logger: () -> Logger
) = Backup({ this }, configPath, logger)

fun ConfigManagerComponentScope.backup(
    configPath: () -> Path,
    logger: () -> Logger
) = Backup({ this.manager }, configPath, logger).also { compose(it) }

fun ConfigManager.backup(
    configPath: Path,
    logger: Logger
) = Backup({ this }, { configPath }, { logger })

fun ConfigManagerComponentScope.backup(
    configPath: Path,
    logger: Logger
) = Backup({ this.manager }, { configPath }, { logger }).also { compose(it) }