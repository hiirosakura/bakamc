package cn.bakamc.folia.event.entity

import cn.bakamc.folia.config.Configs.Entity.DISABLE_ENDERMAN_BREAK_BLOCK
import cn.bakamc.folia.util.logger
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
        if (event.entityType == EntityType.ENDERMAN) {
            if (DISABLE_ENDERMAN_BREAK_BLOCK)
                logger.info("阻止了小黑尝试更改方块状态的事件")
            event.isCancelled = DISABLE_ENDERMAN_BREAK_BLOCK
        }
    }


}