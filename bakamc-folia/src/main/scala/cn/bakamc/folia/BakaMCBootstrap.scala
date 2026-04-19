package cn.bakamc.folia

import cn.bakamc.folia.command.commands
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.bootstrap.{BootstrapContext, PluginBootstrap}
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents

//noinspection UnstableApiUsage
class BakaMCBootstrap extends PluginBootstrap {

  override def bootstrap(context: BootstrapContext): Unit = {
    context.getLifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, { case event: ReloadableRegistrarEvent[Commands] =>
      val registrar = event.registrar()
      commands.foreach(_.register(registrar))
    })
  }
}
