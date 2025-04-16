package cn.bakamc.folia.hook

import cn.bakamc.folia.BakaMCPlugin

object BakaMCHooks:BakaMCHook {

    private val hooks = listOf(
        VaultUnlocked,
    )


    override fun onEnable(plugin: BakaMCPlugin) {
        hooks.forEach {
            it.onEnable(plugin)
        }
    }

    override fun onDisable(plugin: BakaMCPlugin) {
        hooks.forEach {
            it.onDisable(plugin)
        }
    }

}


interface BakaMCHook{

    fun onEnable(plugin: BakaMCPlugin)

    fun onDisable(plugin: BakaMCPlugin)

}