package cn.bakamc.folia.item

import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import net.minecraft.world.item.ItemStack

class SpecialItem(
    val adapters: Map<String, ItemAdapter>,
    val expression: String
) {

    companion object {
        fun deserialize(serializeElement: SerializeElement): SpecialItem {
            serializeElement as SerializeObject
            val adapter = buildMap {
                serializeElement["adapters"]!!.asObject.forEach {
                    this[it.key] = ItemAdapter.deserialize(it.value)
                }
            }
            val expression = serializeElement["expression"]!!.asString
            return SpecialItem(adapter, expression)
        }

        val operator: Array<String> = arrayOf("&&", "||")
    }

    fun isMatch(item: ItemStack): Boolean {
        var result = false
        expression.split(" ").forEachIndexed { index, s ->
            if (index % 2 == 0) {
                val rebellion = s.startsWith("!")
                val adapter= adapters[s.substring(1)]!!
                result = adapter.isMatch(item)
                if (rebellion) result = !result
            } else {

            }
        }

        return result
    }


}