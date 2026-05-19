package cn.bakamc.folia.command

import cn.bakamc.folia.command.dsl.RootCommand
import cn.bakamc.folia.command.dsl.ArgumentScope.*
import org.bukkit.entity.Player
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder
import org.bukkit.inventory.{InventoryView, MenuType}

//noinspection UnstableApiUsage
object QuickUseCommand {

  private def menu[A <: InventoryView](t: MenuType.Typed[A, LocationInventoryViewBuilder[A]], player: Player): InventoryView = {
    t.builder().location(player.getLocation).checkReachable(false).build(player)
  }

  val root: RootCommand = RootCommand("quick_use") {
    "crafting_table" {
      requires("bakamc.quick_use.crafting_table")
      execute {
        senderPlayer.openInventory(menu(MenuType.CRAFTING, senderPlayer))
      }
    }
    "stonecutter" {
      requires("bakamc.quick_use.stonecutter")
      execute {
        senderPlayer.openInventory(menu(MenuType.STONECUTTER, senderPlayer))
      }
    }
    "cartography_table" {
      requires("bakamc.quick_use.cartography_table")
      execute {
        senderPlayer.openInventory(menu(MenuType.CARTOGRAPHY_TABLE, senderPlayer))
      }
    }
    "grindstone" {
      requires("bakamc.quick_use.grindstone")
      execute {
        senderPlayer.openInventory(menu(MenuType.GRINDSTONE, senderPlayer))
      }
    }
    "loom" {
      requires("bakamc.quick_use.loom")
      execute {
        senderPlayer.openInventory(menu(MenuType.LOOM, senderPlayer))
      }
    }
    "smithing_table" {
      requires("bakamc.quick_use.smithing_table")
      execute {
        senderPlayer.openInventory(menu(MenuType.SMITHING, senderPlayer))
      }
    }
    "ender_chest" {
      requires("bakamc.quick_use.ender_chest")
      execute {
        senderPlayer.openInventory(senderPlayer.getEnderChest)
      }
    }

  }

}
