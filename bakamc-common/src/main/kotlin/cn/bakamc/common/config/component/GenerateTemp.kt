package cn.bakamc.common.config.component

import kotlinx.coroutines.runBlocking
import moe.forpleuvoir.nebula.config.manager.ConfigManager
import moe.forpleuvoir.nebula.config.manager.ConfigManagerComponentScope
import moe.forpleuvoir.nebula.config.manager.component.ConfigManagerComponent
import moe.forpleuvoir.nebula.config.persistence.ConfigManagerPersistence
import moe.forpleuvoir.nebula.config.util.ConfigUtil
import org.slf4j.Logger
import java.nio.file.Path

class GenerateTemp(
    val manager: () -> ConfigManager,
    val configPath: () -> Path,
    val persistence: () -> ConfigManagerPersistence,
    val logger: () -> Logger
) : ConfigManagerComponent {

    override fun finishInit() {
        runBlocking {
            runCatching {
                ConfigUtil.run {
                    val fileName = persistence().wrapFileName(manager().key)
                    val file = configFile("$fileName.temp", configPath())
                    writeToFile(persistence().serializeToString(manager().serialization().asObject), file)
                }
            }.onFailure {
                logger().error("模板文件生成失败", it)
            }.onSuccess {
                logger().info("模板文件生成成功")
            }
        }
    }

}

fun ConfigManager.generateTemp(
    configPath: () -> Path,
    persistence: () -> ConfigManagerPersistence,
    logger: () -> Logger
) = GenerateTemp({ this }, configPath, persistence, logger)

fun ConfigManagerComponentScope.generateTemp(
    configPath: () -> Path,
    persistence: () -> ConfigManagerPersistence,
    logger: () -> Logger
) = GenerateTemp({ this.manager }, configPath, persistence, logger).also { compose(it) }

fun ConfigManager.generateTemp(
    configPath: Path,
    persistence: ConfigManagerPersistence,
    logger: Logger
) = GenerateTemp({ this }, { configPath }, { persistence }, { logger })

fun ConfigManagerComponentScope.generateTemp(
    configPath: Path,
    persistence: ConfigManagerPersistence,
    logger: Logger
) = GenerateTemp({ this.manager }, { configPath }, { persistence }, { logger }).also { compose(it) }