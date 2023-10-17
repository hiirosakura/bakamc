package cn.bakamc.folia.item

import cn.bakamc.folia.util.toSerializerObjet
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack

class ItemAdapter(
    val pattern: ItemAdaptPattern,
    val value: SerializeElement
) {

    fun isMatch(item: ItemStack): Boolean {
        return when (pattern) {
            ItemAdaptPattern.Id          -> {
                BuiltInRegistries.ITEM.getKey(item.item).toString() == value.asString
            }

            ItemAdaptPattern.Name        -> {
                item.displayName.string == value.asString
            }

            ItemAdaptPattern.NbtComplete -> {
                item.tag?.let {
                    it.toSerializerObjet()

                    true
                }?:false
            }
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