package cn.bakamc.folia.hook

import cn.bakamc.folia.BakaMCPlugin
import cn.bakamc.folia.util.logger
import net.milkbowl.vault2.chat.Chat
import net.milkbowl.vault2.economy.Economy
import net.milkbowl.vault2.permission.Permission

object VaultUnlocked : BakaMCHook {

    var economy: Economy? = null
        private set

    var permission: Permission? = null
        private set

    var chat: Chat? = null

    override fun onEnable(plugin: BakaMCPlugin) {
        plugin.server.pluginManager.getPlugin("Vault")?.let {
            setupEconomy(plugin)
            setupPermission(plugin)
            setupChat(plugin)
            logger.warn("Vault 插件未加载")
        }
    }

    private fun setupEconomy(plugin: BakaMCPlugin): Boolean {
        val rsp = plugin.server.servicesManager.getRegistration(Economy::class.java) ?: return false
        economy = rsp.getProvider()
        return true
    }

    private fun setupPermission(plugin: BakaMCPlugin): Boolean {
        val rsp = plugin.server.servicesManager.getRegistration(Permission::class.java) ?: return false
        permission = rsp.getProvider()
        return true
    }

    private fun setupChat(plugin: BakaMCPlugin): Boolean {
        val rsp = plugin.server.servicesManager.getRegistration(Chat::class.java) ?: return false
        chat = rsp.getProvider()
        return true
    }

    override fun onDisable(plugin: BakaMCPlugin) {}

}