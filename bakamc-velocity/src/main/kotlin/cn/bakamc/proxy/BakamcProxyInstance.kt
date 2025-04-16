package cn.bakamc.proxy

import cn.bakamc.proxy.command.registerCommands
import cn.bakamc.proxy.config.Configs
import cn.bakamc.proxy.database.initDataBase
import cn.bakamc.proxy.event.velocity.MessageChannelEventListener
import cn.bakamc.proxy.event.velocity.PlayerEventListener
import cn.bakamc.proxy.messagechannel.MessageChannels
import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.coroutines.runBlocking
import moe.forpleuvoir.nebula.common.util.ioLaunch
import org.slf4j.Logger
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

object BakamcProxyInstance {

    lateinit var INSTANCE: BakamcProxy
        private set

    lateinit var server: ProxyServer
        private set

    lateinit var logger: Logger
        private set

    lateinit var dataDirectory: Path
        private set

    @JvmStatic
    fun init(bakamcProxy: BakamcProxy) {
        INSTANCE = bakamcProxy
        server = bakamcProxy.server
        logger = bakamcProxy.log
        dataDirectory = bakamcProxy.dataDirectory
        MessageChannels.register(server)
        runBlocking {
            Configs.onLoaded {
                initDataBase()
            }
            Configs.init(dataDirectory)
        }
        registerEventListener(server)
        registerCommands(server)
    }


    private fun registerEventListener(server: ProxyServer) {
        server.eventManager.apply {
            register(INSTANCE, PlayerEventListener)
            register(INSTANCE, MessageChannelEventListener)
            logger.info("事件监听注册完成")
        }

    }

    fun reload() {
        ioLaunch {
            Configs.load()
        }
    }

    fun runDelayed(delay: Duration, task: () -> Unit) {
        server.scheduler.buildTask(INSTANCE, task).delay(delay.inWholeMilliseconds, TimeUnit.MILLISECONDS).schedule()
    }

    fun runTask(task: () -> Unit) {
        server.scheduler.buildTask(INSTANCE, task).schedule()
    }
}