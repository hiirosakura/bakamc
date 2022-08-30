package cn.bakamc.riguru.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.server.standard.ServerEndpointExporter

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.config

 * 文件名 WebScoketConfig

 * 创建时间 2022/8/30 21:13

 * @author forpleuvoir

 */
@Configuration
@EnableWebSocket
class WebSocketConfig {

	@Bean
	fun serverEndpoint(): ServerEndpointExporter = ServerEndpointExporter()

}