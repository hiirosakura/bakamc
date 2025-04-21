package cn.bakamc.folia.hook

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.hook.placeholder.Placeholder
import cn.bakamc.folia.util.logger

object BakaMCHooks : BakaMCHook {

    private val hooks = listOf(
        VaultUnlocked,
        Placeholder
    )


    override fun onEnable(plugin: BakaMCPlugin) {
        hooks.forEach {
            runCatching {
                it.onEnable(plugin)
            }.onFailure {
                logger.error("hook加载失败", it)
            }
        }
    }

    override fun onDisable(plugin: BakaMCPlugin) {
        hooks.forEach {
            runCatching {
                it.onDisable(plugin)
            }.onFailure {
                logger.error("hook卸载失败", it)
            }
        }
    }

}


interface BakaMCHook {

    fun onEnable(plugin: BakaMCPlugin)

    fun onDisable(plugin: BakaMCPlugin)

}