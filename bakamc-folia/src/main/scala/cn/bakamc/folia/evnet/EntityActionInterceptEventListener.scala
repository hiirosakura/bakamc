package cn.bakamc.folia.evnet

import org.bukkit.event.{EventHandler, EventPriority, Listener}
import org.bukkit.event.entity.{EntityChangeBlockEvent, EntityExplodeEvent, EntityPickupItemEvent}
import cn.bakamc.folia.config.EntityConfig.*

import scala.jdk.CollectionConverters.*

object EntityActionInterceptEventListener extends Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  def entityChangedBlockEvent(event: EntityChangeBlockEvent): Unit = {
    val entity = event.getEntity
    val block = event.getBlock
    val to = event.getTo.getKey.toString
    val result = changeBlockCache.exists { case (matcher, cs) =>
      matcher.test(entity) && cs.exists { m =>
        m.test(block, to)
      }
    }
    if (result) {
      event.setCancelled(true)
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  def entityExplodeEvent(event: EntityExplodeEvent): Unit = {
    val entity = event.getEntity
    val blockList = event.blockList().asScala
    explodeBlockCache.view.filter { case (matcher, _) =>
      matcher.test(entity)
    }.foreach { case (_, bs) =>
      blockList.filter(b => bs.exists(_.test(b)))
        .foreach { e =>
          event.blockList().remove(e)
        }
    }
  }

  @EventHandler
  def onEntityPickupItem(event: EntityPickupItemEvent): Unit = {
    val entity = event.getEntity
    val item = event.getItem
    val result = pickupItemCache.exists { case (matcher, is) =>
      matcher.test(entity) && is.exists(_.test(item))
    }
    if (result) {
      event.setCancelled(true)
    }
  }
}
