package cn.bakamc.folia

import org.bukkit.entity.Entity

import scala.jdk.CollectionConverters.CollectionHasAsScala

extension (self: Entity) {

  def execute(delay: Long = 1)(run: => Unit): Unit = {
    self.getScheduler.execute(BakaMC, () => run, null, delay)
  }

}

def onlinePlayers = BakaMC.getServer.getOnlinePlayers.asScala