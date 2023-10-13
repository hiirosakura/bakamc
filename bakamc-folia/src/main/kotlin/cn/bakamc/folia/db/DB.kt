package cn.bakamc.folia.db

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.config.Configs
import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.core.MybatisConfiguration
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import org.apache.ibatis.datasource.pooled.PooledDataSource
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.plugin.Interceptor
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import javax.sql.DataSource


fun init() {


}


fun initSession(): SqlSession {
    return SqlSessionFactoryBuilder().build(MybatisConfiguration().apply {
        addInterceptor(initInterceptor())
        addMappers("cn.bakamc.folia.db.mapper")

        GlobalConfigUtils.getGlobalConfig(this).apply {
            sqlInjector = DefaultSqlInjector()
            identifierGenerator = DefaultIdentifierGenerator.getInstance()
            superMapperClass = BaseMapper::class.java
        }

        environment = Environment("BakaMC", JdbcTransactionFactory(), initDataSource())
    }).openSession()

}

private fun initDataSource(): DataSource {
    return PooledDataSource().apply {
        driver = "com.mysql.cj.jdbc.Driver"
        url = Configs.Database.URL
        username = Configs.Database.USER
        password = Configs.Database.PASSWORD
    }
}

private fun initInterceptor(): Interceptor {
    return MybatisPlusInterceptor().apply {
        addInnerInterceptor(PaginationInnerInterceptor().apply {
            dbType = DbType.MYSQL
            isOverflow = true
            maxLimit = 500L
        })
    }
}
