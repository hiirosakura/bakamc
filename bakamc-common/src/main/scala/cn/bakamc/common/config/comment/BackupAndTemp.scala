package cn.bakamc.common.config.comment

import moe.forpleuvoir.nebula.config.component.ConfigManagerComponent
import moe.forpleuvoir.nebula.config.persistence.ConfigPersistence
import moe.forpleuvoir.nebula.config.{ConfigManager, ConfigUtil}
import org.slf4j.Logger

import java.nio.file.{Files, Path, StandardCopyOption}

class BackupAndTemp(
  path: => Path,
  logger: => Logger,
  persistence: => ConfigPersistence,
  override val manager: ConfigManager
) extends ConfigManagerComponent {

  override def beginInit(): Unit = {
    try {
      val fileName = persistence.wrapFileName(manager.name)
      val backupFile = ConfigUtil.configFile(s"$fileName.backup", path).toPath
      val file = ConfigUtil.configFile(fileName, path).toPath

      if (Files.exists(file)) {
        Files.copy(file, backupFile, StandardCopyOption.REPLACE_EXISTING)
        logger.info(s"Success to create backup file")
      }
    }
    catch case e: Throwable => logger.warn(s"Failed to create backup file", e)

  }

  override def finishInit(): Unit = {
    try {
      val fileName = persistence.wrapFileName(manager.name)
      val file = ConfigUtil.configFile(s"$fileName.temp", path)
      ConfigUtil.writeToFile(persistence.dataToString(manager.serialization), file)
      logger.info("Success to create temp file")
    } catch case e: Throwable => logger.warn(s"Failed to create temp file", e)
  }

}

object BackupAndTemp {
  def apply(path: Path, logger: Logger, persistence: ConfigPersistence)(using manager: ConfigManager): BackupAndTemp =
    manager.addComponents(new BackupAndTemp(path, logger, persistence, manager))
}