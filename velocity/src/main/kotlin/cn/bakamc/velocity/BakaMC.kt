package cn.bakamc.velocity

import cn.bakamc.velocity.event.PlayerEvent
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import java.nio.file.Path


/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.velocity

 * 文件名 BakaMC

 * 创建时间 2022/11/27 15:27

 * @author forpleuvoir

 */
@Plugin(id = "bakamc")
class BakaMC @Inject constructor(private val server: ProxyServer, private val logger: Logger, @DataDirectory private val dataDirectory: Path) {

	@Subscribe
	fun onProxyInitialization(event: ProxyInitializeEvent) {
		logger.info("[BakaMC]服务启动")
		server.eventManager.register(this, PlayerEvent(server))

	}
}