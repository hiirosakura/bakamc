package cn.bakamc.folia.database

import cn.bakamc.folia.config.DataBaseConfig
import cn.bakamc.folia.database.table.{flightEnergies, playerInfos, specialItems}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.slf4j.Logger
import slick.dbio.NoStream
import slick.jdbc.MySQLProfile.api.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object DatabaseManager {
  private var dataSource: Option[HikariDataSource] = None
  private var database: Option[Database] = None

  def init(): Unit = {
    if (dataSource.isDefined) {
      throw new IllegalStateException("Database already initialized")
    }

    val config = new HikariConfig()
    config.setJdbcUrl(DataBaseConfig.url.value)
    config.setUsername(DataBaseConfig.username.value)
    config.setPassword(DataBaseConfig.password.value)
    config.setMaximumPoolSize(DataBaseConfig.DataSource.maxPoolSize.value)
    config.setMinimumIdle(DataBaseConfig.DataSource.minIdleTime.value)
    config.setConnectionTimeout(DataBaseConfig.DataSource.connectionTimeout.value)
    config.setIdleTimeout(DataBaseConfig.DataSource.idleTimeout.value)
    config.setMaxLifetime(DataBaseConfig.DataSource.maxLifetime.value)
    config.setKeepaliveTime(DataBaseConfig.DataSource.keepAliveTime.value)
    config.addDataSourceProperty("cachePrepStmts", "true")
    config.addDataSourceProperty("prepStmtCacheSize", "250")
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

    dataSource = Some(new HikariDataSource(config))
    database = Some(Database.forDataSource(dataSource.get, None))
  }

  def db: Database = database.getOrElse(throw new IllegalStateException("Database not initialized"))

  def reload(): Unit = {
    if (!isInitialized) {
      throw new IllegalStateException("Database not initialized")
    }
    shutdown()
    init()
  }

  def shutdown(): Unit = {
    database.foreach(_.close())
    dataSource.foreach(_.close())
    dataSource = None
    database = None
  }

  def isInitialized: Boolean = database.isDefined
}

def db[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = DatabaseManager.db.run(a)


def initDatabase(logger: Logger): Future[Unit] = {
  DatabaseManager.init()

  val schema = playerInfos.schema ++ flightEnergies.schema ++ specialItems.schema

  val setupAction = schema.createIfNotExists
  val result = db(setupAction)
  result.onComplete {
    case Success(value) => logger.info("[Database] 所有表已就绪(自动创建或已存在)")
    case Failure(exception) =>
      logger.error(s"[Database] 初始化表时出错: ${exception.getMessage}", exception)
  }
  result
}