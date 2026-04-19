package cn.bakamc.folia.config

import cn.bakamc.common.config.comment.BackupAndTemp
import moe.forpleuvoir.nebula.config.component.LocalConfig
import moe.forpleuvoir.nebula.config.persistence.HJsonConfigPersistence
import moe.forpleuvoir.nebula.config.{ConfigManager, ExceptionHandler}
import org.slf4j.Logger

import java.nio.file.Path

class PluginConfigManager(
  name: String,
) extends ConfigManager(name)(using PluginExceptionHandler) {

  private var _logger: Option[Logger] = None

  private[config] def logger: Logger = _logger.get

  private var isInitialized = false

  def setup(path: Path, logger: Logger): Unit = {
    if (isInitialized) throw new IllegalStateException("Already initialized")
    _logger = Some(logger)

    val persistence = HJsonConfigPersistence(this)
    LocalConfig(path, persistence)
    BackupAndTemp(path, logger, persistence)

    this.startup()

    isInitialized = true
  }


}
