package cn.bakamc.folia.item.customdata

import cn.bakamc.folia.util.asNMS
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import org.bukkit.inventory.ItemStack

object BakaData {

    const val KEY = "bakamc_data"

    fun fromItemStack(itemStack: ItemStack): CompoundTag? {
        return itemStack.asNMS.components.get(DataComponents.CUSTOM_DATA)?.let {
            fromNbt(it.copyTag())
        }
    }

    fun fromNbt(nbt: CompoundTag): CompoundTag? {
        return (nbt.get(KEY) as? CompoundTag)?.copy()
    }


}