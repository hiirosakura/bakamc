package cn.bakamc.folia.util.text

import cn.bakamc.common.ComponentAdapter
import cn.bakamc.common.extension.Text
import cn.bakamc.common.extension.Text.given
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
      case b: Boolean => Text.boolean(b)
      case s: String => Text.literal(s)
      case i: Int => Text.literal(i.toString).color(0x00FF00)         // 绿色 - 常规数值
      case l: Long => Text.literal(l.toString).color(0xFFD700)        // 金色 - 大数值
      case l: BigInt => Text.literal(l.toString).color(0xFFD700)      // 金色 - 大数值
      case f: Float => Text.literal(f.toString).color(0x00BFFF)       // 深天蓝 - 小数
      case d: Double => Text.literal(d.toString).color(0x9370DB)      // 紫色 - 高精度
      case d: BigDecimal => Text.literal(d.toString).color(0x9370DB)  // 紫色 - 高精度
      case s: Short => Text.literal(s.toString).color(0x87CEEB)       // 天蓝 - 小整数
      case b: Byte => Text.literal(b.toString).color(0x98FB98)        // 苍绿 - 微小值
      case b: Block => Component.translatable(b.getType.translationKey()).wrapInSquareBrackets
      case b: net.minecraft.world.level.block.Block => b.getName
      case i: ItemStack => i.hoveredNameWithCount
      case i: net.minecraft.world.item.ItemStack => CraftItemStack.asBukkitCopy(i).hoveredNameWithCount
      case p: Player => p.nameAsComponent
      case p: ServerPlayer => p.nameAsComponent
      case _ => Component.text(input.toString)
    }
  }

}


