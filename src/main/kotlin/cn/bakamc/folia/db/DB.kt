package cn.bakamc.folia.db

import cn.bakamc.folia.config.Configs
import com.zaxxer.hikari.HikariDataSource
import org.ktorm.database.Database
import org.ktorm.support.mysql.MySqlDialect
import javax.sql.DataSource
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


val dataSource: DataSource by lazy {
    HikariDataSource().apply {
        driverClassName = "com.mysql.cj.jdbc.Driver"
        jdbcUrl = Configs.Database.URL
        username = Configs.Database.USER
        password = Configs.Database.PASSWORD
    }
}

val database get() = Database.connect(dataSource, MySqlDialect())

@OptIn(ExperimentalContracts::class)
inline fun <R> database(action: Database.() -> R): R {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    return action(Database.connect(dataSource, MySqlDialect()))
}
