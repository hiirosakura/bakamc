package cn.bakamc.folia

import cn.bakamc.common.config.Configs
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Path

object BakaMCPlugin: JavaPlugin() {

    override fun onEnable() {
        Configs.init(Path.of(""))
    }
}