package cn.bakamc.folia

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.bootstrap.{BootstrapContext, PluginBootstrap}
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents

//noinspection UnstableApiUsage
class BakaMCBootstrap extends PluginBootstrap {

  override def bootstrap(context: BootstrapContext): Unit = {
    context.getLifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, { case event: ReloadableRegistrarEvent[Commands] =>
      //TODO 在这里注册指令
    })
  }
}
