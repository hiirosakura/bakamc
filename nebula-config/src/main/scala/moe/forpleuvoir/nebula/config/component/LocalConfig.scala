package moe.forpleuvoir.nebula.config.component

import moe.forpleuvoir.nebula.config.persistence.ConfigPersistence
import moe.forpleuvoir.nebula.config.{ConfigManager, ConfigUtil}

import java.nio.file.Path

class LocalConfig(
  configPath: => Path,
  persistence: => ConfigPersistence,
  override val manager: ConfigManager
) extends ConfigManagerComponent {

  override def onSave(): Unit = {
    if (!manager.savable) return
    onForcedSave()
  }

  override def onForcedSave(): Unit = {
    val file = ConfigUtil.configFile(persistence.wrapFileName(manager.name), configPath)
    ConfigUtil.writeToFile(persistence.dataToString(manager.serialization), file)
    manager.markSaved()
  }

  override def onLoad(): Unit = {
    val file = ConfigUtil.configFile(persistence.wrapFileName(manager.name), configPath)
    val data = persistence.stringToData(ConfigUtil.readFileToString(file))
    manager.deserialization(data)
  }

}

object LocalConfig {

  def apply(
    configPath: Path,
    persistence: ConfigPersistence
  )(using manager: ConfigManager): LocalConfig = {
    manager.addComponents(new LocalConfig(configPath, persistence, manager))
  }

}