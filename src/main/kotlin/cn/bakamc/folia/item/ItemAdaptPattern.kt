package cn.bakamc.folia.item

import moe.forpleuvoir.nebula.serialization.base.SerializeElement

enum class ItemAdaptPattern(val description: String) {
    Id("匹配物品ID，例如 minecraft:stone"),
    Name("匹配物品名称，例如 stone,纯文本 忽略文本样式"),
    NbtComplete("匹配物品标签，完全匹配"),
    NbtContains("匹配物品标签，部分匹配");

    companion object {

        fun byName(name: String): ItemAdaptPattern? {
            return entries.find { it.name == name }
        }

        fun deserialize(name: SerializeElement): ItemAdaptPattern {
            return byName(name.asString)!!
        }

    }
}