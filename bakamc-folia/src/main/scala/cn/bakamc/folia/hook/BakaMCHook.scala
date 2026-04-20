package cn.bakamc.folia.hook

import cn.bakamc.folia.BakaMC

trait BakaMCHook {

  def onEnable(plugin: BakaMC): Unit

  def onDisable(plugin: BakaMC): Unit

}

object BakaMCHook extends BakaMCHook {

  private val hooks: List[BakaMCHook] = List(
    VaultUnlocked
  )

  override def onEnable(plugin: BakaMC): Unit =
    hooks.foreach { hook =>
      try hook.onEnable(plugin)
      catch {
        case e: Throwable =>
          plugin.logger.warn(s"Hook ${hook.getClass.getSimpleName} failed to load.", e)
      }
    }

  override def onDisable(plugin: BakaMC): Unit =
    hooks.foreach(_.onDisable(plugin))
}