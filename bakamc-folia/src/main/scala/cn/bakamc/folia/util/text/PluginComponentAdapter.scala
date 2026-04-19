package cn.bakamc.folia.util.text

import cn.bakamc.common.ComponentAdapter
import cn.bakamc.folia.util.player.PlayerConversion.given
import cn.bakamc.folia.util.text.ItemExtensions.*
import cn.bakamc.folia.util.text.PlayerExtension.nameAsComponent
import net.kyori.adventure.text.Component
import net.minecraft.network.chat.Component as MCComponent
import net.minecraft.server.level.ServerPlayer
import org.bukkit.block.Block
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object PluginComponentAdapter extends ComponentAdapter {
  given ComponentAdapter = PluginComponentAdapter

  override def convert(input: Any): Component = {
    input match {
      case c: Component => c
      case c: MCComponent => c
      case s: String => Component.text(s)
      case b: Block => Component.translatable(b.getType.translationKey()).wrapInSquareBrackets
      case b: net.minecraft.world.level.block.Block => b.getName
      case i: ItemStack => i.itemNameWithCount
      case i: net.minecraft.world.item.ItemStack => CraftItemStack.asBukkitCopy(i).itemNameWithCount
      case p: Player => p.nameAsComponent
      case p: ServerPlayer => p.nameAsComponent
      case _ => Component.text(input.toString)
    }
  }

}


