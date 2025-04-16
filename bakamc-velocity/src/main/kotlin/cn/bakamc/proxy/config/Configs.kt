package cn.bakamc.proxy.config

import cn.bakamc.common.ServerInfo
import cn.bakamc.common.config.component.backup
import cn.bakamc.common.config.component.generateTemp
import cn.bakamc.common.config.item.serverInfo
import cn.bakamc.proxy.BakamcProxyInstance
import cn.bakamc.proxy.BakamcProxyInstance.logger
import moe.forpleuvoir.nebula.config.manager.ConfigManagerImpl
import moe.forpleuvoir.nebula.config.manager.component.localConfig
import moe.forpleuvoir.nebula.config.manager.components
import moe.forpleuvoir.nebula.config.persistence.ConfigManagerPersistence
import moe.forpleuvoir.nebula.config.persistence.jsonPersistence
import java.nio.file.Path

object Configs : ConfigManagerImpl(BakamcProxyInstance.INSTANCE.bakaName) {

    /**
     * 配置文件路径
     * @param path Path
     */
    suspend fun init(path: Path) {
        components {
            val persistence: ConfigManagerPersistence = jsonPersistence()
            localConfig(path, persistence)
            backup(path, logger)
            generateTemp(path, persistence, logger)
        }

        init()
        runCatching {
            load()
            if (this.savable()) {
                save()
            }
        }.onFailure {
            it.printStackTrace()
            forceSave()
        }
    }

    val SERVER_INFO by serverInfo("server_info", ServerInfo("serve_name"))

    init {
        addConfig(DatabaseConfig)
        addConfig(MiscConfig)
//        addConfig(BotConfig)
        addConfig(IpRestrictConfig)
    }


}