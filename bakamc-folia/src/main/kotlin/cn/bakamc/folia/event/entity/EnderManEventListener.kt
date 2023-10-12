package cn.bakamc.folia.event.entity

import cn.bakamc.folia.config.Configs
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent

object EnderManEventListener : Listener {

    /**
     * 末影人是否可以破坏方块
     * @param event EntityChangeBlockEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun endermanCanBreakBlock(event: EntityChangeBlockEvent) {
        if (event.entityType == EntityType.ENDERMAN ) {
            event.isCancelled = Configs.Entity.ENDERMAN_CAN_BREAK_BLOCK
        }
    }


}