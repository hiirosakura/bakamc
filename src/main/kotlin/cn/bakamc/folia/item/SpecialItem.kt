package cn.bakamc.folia.item

import cn.bakamc.folia.config.Configs.stringToSerializeObject
import cn.bakamc.folia.util.logger
import moe.forpleuvoir.nebula.config.util.ConfigUtil
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import net.minecraft.world.item.ItemStack
import java.nio.file.Path
import java.util.logging.Level

class SpecialItem(
    val adapters: Map<String, ItemAdapter>,
    val expression: String
) {

    companion object {

        private const val fileName = "special_items.json"

        private fun deserialize(serializeElement: SerializeElement): SpecialItem {
            serializeElement as SerializeObject
            val adapter = buildMap {
                serializeElement["adapters"]!!.asObject.forEach {entry->
                    runCatching {
                        this[entry.key] = ItemAdapter.deserialize(entry.value)
                    }.onFailure {
                        logger.log(Level.WARNING, "Failed to deserialize item adapter[${entry.key}]: ${it.message}", it)
                    }
                }
            }
            val expression = serializeElement["expression"]!!.asString
            return SpecialItem(adapter, expression)
        }

        lateinit var specialItems: Map<String, SpecialItem>
            private set

        fun init(path: Path) {
            runCatching {
                stringToSerializeObject(readFile(path)).apply {
                    specialItems = buildMap {
                        this@apply.forEach { k, v ->
                            this[k] = deserialize(v)
                        }
                    }
                }
            }.onSuccess {
                logger.info("特殊物品加载成功")
            }.onFailure {
                specialItems = emptyMap()
                it.printStackTrace()
                logger.info("特殊物品加载失败")
            }
        }

        private fun readFile(path: Path): String {
            ConfigUtil.apply {
                return readFileToString(configFile(fileName, path))
            }
        }

    }

    fun isMatch(item: ItemStack): Boolean {
        var result = false
        var lastOperator = ""
        expression.split(" ").forEachIndexed { index, s ->
            if (index % 2 == 0) {
                val rebellion = s.startsWith("!")
                val adapter = adapters[s.substring(1)]!!
                val isMatch = adapter.isMatch(item)
                result = when (lastOperator) {
                    "&&" -> {
                        isMatch and result
                    }

                    "||" -> {
                        isMatch || result
                    }

                    else -> isMatch
                }
                if (rebellion) result = !result

            } else {
                lastOperator = s
            }
        }

        return result
    }


}