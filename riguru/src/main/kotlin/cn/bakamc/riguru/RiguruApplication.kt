package cn.bakamc.riguru

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * Main

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru

 * 文件名 RiguruApplication

 * 创建时间 2022/8/30 14:55

 * @author forpleuvoir

 */
@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
@ConfigurationPropertiesScan
@MapperScan("cn.bakamc.riguru.mapper")
class RiguruApplication

fun main(args: Array<String>) {
	runApplication<RiguruApplication>(*args)
}


