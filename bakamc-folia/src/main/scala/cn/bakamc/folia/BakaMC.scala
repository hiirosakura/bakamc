package cn.bakamc.folia

import cn.bakamc.folia.BakaMC.instance
import cn.bakamc.folia.config.{AnvilTextParserConfig, Configs, DataBaseConfig, PluginExceptionHandler}
import cn.bakamc.folia.database.{DatabaseManager, initDatabase}
import cn.bakamc.folia.functional.flightenergy.FlightEnergyManager
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.{Logger, LoggerFactory}

import scala.compiletime.uninitialized

class BakaMC extends JavaPlugin {

  val logger: Logger = LoggerFactory.getLogger("BakaMC")

  override def onEnable(): Unit = {
    logger.info("BakaMC加载中")
    PluginExceptionHandler.logger = logger


    //region 初始化配置
    Configs.setup(getDataPath, logger)
    DataBaseConfig.setup(getDataPath, logger)
    AnvilTextParserConfig.setup(getDataPath, logger)

    Configs.onLoad { dur =>
      FlightEnergyManager.reload()
    }
    DataBaseConfig.onLoad { dur =>
      DatabaseManager.reload()
      FlightEnergyManager.reload()
    }

    //endregion

    try {
      initDatabase(logger)
      logger.info("数据库连接成功")
    } catch {
      case e: Exception =>
        logger.error("数据库初始化失败", e)
    }

    instance = this
  }


  override def onDisable(): Unit = {
    logger.info("BakaMC关闭中")

    FlightEnergyManager.onDisable()

    if (DatabaseManager.isInitialized) {
      DatabaseManager.shutdown()
      logger.info("数据库已关闭")
    }

  }

}


object BakaMC {

  private var _instance: BakaMC = uninitialized

  private def instance_=(value: BakaMC): Unit = _instance = value

  def instance: BakaMC = _instance

  given Conversion[BakaMC.type, BakaMC] = _.instance

}