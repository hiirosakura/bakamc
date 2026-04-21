package cn.bakamc.folia.util.text

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

object ItemExtensions {

  extension (item: ItemStack) {
    def itemName: Component = {
      val meta = item.getItemMeta
      if (meta != null && meta.hasItemName) {
        meta.itemName().color(meta.getRarity.color())
      } else {
        Component.translatable(item.getType.translationKey()).color(meta.getRarity.color())
      }
    }

    def itemNameWithCount: Component = {
      val name = itemName
      if (item.getAmount >= 1) {
        name.wrapInSquareBrackets
      } else {
        Component.text("[")
          .append(name)
          .append(s" x${item.getAmount}")
          .append("]")
      }
    }

    def hoveredName: Component = {
      itemName.hoverEvent(item.asHoverEvent())
    }

    def hoveredNameWithCount: Component = {
      itemNameWithCount.hoverEvent(item.asHoverEvent())
    }

  }

}
