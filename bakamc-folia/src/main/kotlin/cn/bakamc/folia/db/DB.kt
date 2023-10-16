package cn.bakamc.folia.db

import cn.bakamc.folia.config.Configs
import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor
import com.zaxxer.hikari.HikariDataSource
import org.apache.ibatis.logging.slf4j.Slf4jImpl
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import javax.sql.DataSource
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun init(){
    sqlSessionFactory.openSession().use {  }
}

val sqlSessionFactory: SqlSessionFactory by lazy {
    initSqlSessionFactory()
}

@OptIn(ExperimentalContracts::class)
inline fun session(action: SqlSession.() -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    sqlSessionFactory.openSession(true).use(action)
}

private fun initSqlSessionFactory(): SqlSessionFactory {
    return MybatisSqlSessionFactoryBuilder().build(MybatisConfiguration(environment()).apply { config() })
}

private fun environment(): Environment {
    return Environment("Production", JdbcTransactionFactory(), dataSource())
}

private fun MybatisConfiguration.config(): MybatisConfiguration {

    addMappers("cn.bakamc.folia.db.mapper")
    addInterceptor(PaginationInterceptor())

    logImpl = Slf4jImpl::class.java

    return this
}

private fun dataSource(): DataSource {
    return HikariDataSource().apply {
        driverClassName = "com.mysql.cj.jdbc.Driver"
        jdbcUrl = Configs.Database.URL
        username = Configs.Database.USER
        password = Configs.Database.PASSWORD
    }
}
