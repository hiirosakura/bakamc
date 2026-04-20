package cn.bakamc.folia.hook

import cn.bakamc.folia.BakaMC
import net.milkbowl.vault2.chat.Chat
import net.milkbowl.vault2.economy.Economy
import net.milkbowl.vault2.permission.Permission

object VaultUnlocked extends BakaMCHook {

  private var _economy: Option[Economy] = None

  def economy: Economy = _economy.get

  private var _permission: Option[Permission] = None

  def permission: Permission = _permission.get

  private var _chat: Option[Chat] = None

  def chat: Chat = _chat.get

  override def onEnable(plugin: BakaMC): Unit = {
    val vault = plugin.getServer.getPluginManager.getPlugin("Vault")
    if (vault == null) {
      plugin.logger.warn("未找到Vault插件")
      return
    }
    setupEconomy(plugin)
    setupPermission(plugin)
    setupChat(plugin)
  }


  private def setupEconomy(plugin: BakaMC): Unit = {
    val rsp = plugin.getServer.getServicesManager.getRegistration(classOf[Economy])
    if (rsp == null) {
      return
    }
    _economy = Option(rsp.getProvider)
  }

  private def setupPermission(plugin: BakaMC): Unit = {
    val rsp = plugin.getServer.getServicesManager.getRegistration(classOf[Permission])
    if (rsp == null) {
      return
    }
    _permission = Option(rsp.getProvider)
  }

  private def setupChat(plugin: BakaMC): Unit = {
    val rsp = plugin.getServer.getServicesManager.getRegistration(classOf[Chat])
    if (rsp == null) {
      return
    }
    _chat = Option(rsp.getProvider)
  }

  override def onDisable(plugin: BakaMC): Unit = {}

}
