package cn.bakamc.folia.util.text

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

object ItemExtensions {

  extension (item: ItemStack) {
    def itemName: Component = {
      val meta = item.getItemMeta
      if (meta != null && meta.hasDisplayName) {
        meta.displayName()
      } else {
        Component.translatable(item.getType.translationKey())
      }
    }

    def itemNameWithCount: Component = {
      val name = itemName
      if (item.getAmount >= 1) {
        name
      } else {
        Component.text("[")
          .append(name)
          .append(s" x${item.getAmount}")
          .append("]")
      }
    }
  }

}
