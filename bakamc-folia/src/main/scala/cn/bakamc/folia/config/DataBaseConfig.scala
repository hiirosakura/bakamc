package cn.bakamc.folia.config

import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.{ConfigInt, ConfigLong, ConfigString}

object DataBaseConfig extends PluginConfigManager("database") {

  val url = ConfigString("jdbcUrl", "jdbc:mysql://localhost:3306/bakamc?allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai")

  val username = ConfigString("username", "root")

  val password = ConfigString("password", "root")

  object DataSource extends ConfigGroup("data_source") {

    val connectionTimeout = ConfigLong("connection_timeout", 60000)

    val idleTimeout = ConfigLong("idle_timeout", 60000)

    val maxPoolSize = ConfigInt("max_pool_size", 30)

    val maxLifetime = ConfigLong("max_lifetime", 1800000)

    val keepAliveTime = ConfigLong("keep_alive_time", 0)

    val minIdleTime = ConfigInt("min_idle_time", 5)

  }

}
