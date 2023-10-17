package cn.bakamc.folia.item

import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemAdapter(
    val pattern: ItemAdaptPattern,
    val value: SerializeElement
) {

    fun isMatch(item:ItemStack):Boolean{
        return when (pattern) {
            ItemAdaptPattern.Id          -> {
                Material.matchMaterial(value.asString) == item.type
            }
            ItemAdaptPattern.Name        -> {
                item.displayName(
            }
            ItemAdaptPattern.NbtComplete -> TODO()
            ItemAdaptPattern.NbtPart     -> TODO()
        }
    }

    companion object {
        fun deserialize(element: SerializeElement): ItemAdapter {
            element as SerializeObject
            val pattern = ItemAdaptPattern.deserialize(element["pattern"]!!)
            val value = element["value"]!!
            return ItemAdapter(pattern, value)
        }
    }
}