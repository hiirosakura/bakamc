package cn.bakamc.folia.event

import cn.bakamc.common.text.bakatext.BakaText
import cn.bakamc.folia.config.MiscConfig
import cn.bakamc.folia.config.MiscConfig.ENABLE_PLAYER_INTERACT_MODIFY
import cn.bakamc.folia.config.MiscConfig.quick_block_use
import cn.bakamc.folia.flight_energy.FlightEnergyManager
import cn.bakamc.folia.item.customdata.InteractInterceptHandler
import cn.bakamc.folia.item.customdata.LeftClickBlock
import cn.bakamc.folia.item.customdata.LeftClickEntity
import cn.bakamc.folia.item.customdata.RightClickBlock
import cn.bakamc.folia.service.PlayerService
import cn.bakamc.folia.util.ioLaunch
import cn.bakamc.folia.util.logger
import moe.forpleuvoir.nebula.common.util.primitive.ifc
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack

object PlayerEventListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!MiscConfig.ENABLE_PLAYER_JOIN_MESSAGE) {
            event.joinMessage(null)
        }
        ioLaunch {
            runCatching {
                PlayerService.insertOrUpdate(event.player)
                FlightEnergyManager.onPlayerJoin(event.player)
            }.onSuccess {
                logger.info("玩家加入游戏")
            }.onFailure {
                logger.error("数据库错误", it)
            }
        }
    }

    @EventHandler
    fun onChangeWorld(event: PlayerChangedWorldEvent) {
        FlightEnergyManager.onWorldChanged(event)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (!MiscConfig.ENABLE_PLAYER_QUIT_MESSAGE) {
            event.quitMessage(null)
        }
        ioLaunch {
            runCatching {
                FlightEnergyManager.onPlayerQuit(event.player)
            }.onSuccess {
                logger.info("玩家加退出游戏")
            }.onFailure {
                logger.error("数据库错误", it)
            }
        }
    }

    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        FlightEnergyManager.onPlayerRespawn(event.player)
    }

    @EventHandler
    fun onPlayerGameModeChange(event: PlayerGameModeChangeEvent) {
        if (event.player.isOnline) {
            FlightEnergyManager.onPlayerGameModeChange(event.player, event.newGameMode)
        }
    }

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        //------------ 交互限制 ------------\\
        if (ENABLE_PLAYER_INTERACT_MODIFY && event.item != null) {
            val intercept = when (event.action) {
                Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR   -> InteractInterceptHandler.handlerInteract(event.item!!, LeftClickBlock(event.clickedBlock))
                Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR -> InteractInterceptHandler.handlerInteract(event.item!!, RightClickBlock(event.clickedBlock))
                Action.PHYSICAL                                  -> false
            }
            if (intercept) {
                event.isCancelled = true
            }
        }
        //------------ 直接打开功能方块 ------------\\
        if (event.action == Action.RIGHT_CLICK_AIR) {
            val player = event.player
            event.item?.let { item ->
                quickUse(item, player).ifc {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onPlayerAttackEntity(event: EntityDamageByEntityEvent) {
        if (ENABLE_PLAYER_INTERACT_MODIFY && event.damager is Player) {
            val player = event.damager as Player
            if (InteractInterceptHandler.handlerInteract(player.inventory.itemInMainHand, LeftClickEntity(event.entity))) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerRightClickEntity(event: PlayerInteractEntityEvent) {
        if (ENABLE_PLAYER_INTERACT_MODIFY) {
            if (InteractInterceptHandler.handlerInteract(event.player.inventory.getItem(event.hand), LeftClickEntity(event.rightClicked))) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val clicker = event.whoClicked
        if (event.inventory.type in quick_block_use.INVENTORY_TYPE && event.click == ClickType.RIGHT && clicker is Player) {
            event.currentItem?.let { item ->
                quickUse(item, clicker).ifc {
                    clicker.updateInventory()
                    event.isCancelled = true
                }
            }
        }
    }

    private fun quickUse(itemStack: ItemStack, player: Player): Boolean {
        return when (val type = itemStack.type) {
            Material.CRAFTING_TABLE    -> if (player.hasPermission("bakamc.quick_use.crafting_table")) {
                openBlock(type, player)
                true
            } else false

            Material.STONECUTTER       -> if (player.hasPermission("bakamc.quick_use.stonecutter")) {
                openBlock(type, player)
                true
            } else false

            Material.CARTOGRAPHY_TABLE -> if (player.hasPermission("bakamc.quick_use.cartography_table")) {
                openBlock(type, player)
                true
            } else false

            Material.GRINDSTONE        -> if (player.hasPermission("bakamc.quick_use.grindstone")) {
                openBlock(type, player)
                true
            } else false

            Material.LOOM              -> if (player.hasPermission("bakamc.quick_use.loom")) {
                openBlock(type, player)
                true
            } else false

            Material.SMITHING_TABLE    -> if (player.hasPermission("bakamc.quick_use.smithing_table")) {
                openBlock(type, player)
                true
            } else false

            Material.ENDER_CHEST       -> if (player.hasPermission("bakamc.quick_use.ender_chest")) {
                openBlock(type, player)
                true
            } else false

            else                       -> false
        }
    }

    private fun openBlock(type: Material, player: Player) {
        when (type) {
            Material.CRAFTING_TABLE    -> {
                player.openWorkbench(player.location, true)
            }

            Material.STONECUTTER       -> {
                player.openStonecutter(player.location, true)
            }

            Material.CARTOGRAPHY_TABLE -> {
                player.openCartographyTable(player.location, true)
            }

            Material.GRINDSTONE        -> {
                player.openGrindstone(player.location, true)
            }

            Material.LOOM              -> {
                player.openLoom(player.location, true)
            }

            Material.SMITHING_TABLE    -> {
                player.openSmithingTable(player.location, true)
            }

            Material.ENDER_CHEST       -> {
                player.openInventory(player.enderChest)
            }

            else                       -> Unit
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (MiscConfig.player_death_message.ENABLE_DEATH_MESSAGE_OVERRIDE) {
            val prefix = BakaText.parse(MiscConfig.player_death_message.DEATH_MESSAGE_PREFIX)
            val suffix = BakaText.parse(MiscConfig.player_death_message.DEATH_MESSAGE_SUFFIX)
            prefix.append(event.deathMessage() ?: Component.empty())
                .append(suffix)
                .let {
                    event.deathMessage(it)
                }
        }
    }
}

