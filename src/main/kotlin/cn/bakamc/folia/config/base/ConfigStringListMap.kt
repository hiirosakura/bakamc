package cn.bakamc.folia.config.base

import moe.forpleuvoir.nebula.common.util.NotifiableArrayList
import moe.forpleuvoir.nebula.common.util.NotifiableLinkedHashMap
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.config.item.ConfigMutableMapValue
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject

class ConfigStringListMap(
    override val key: String,
    defaultValue: Map<String, List<String>>
) : ConfigBase<MutableMap<String, List<String>>, ConfigStringListMap>(), ConfigMutableMapValue<String, List<String>> {

    override val defaultValue: MutableMap<String, List<String>> = LinkedHashMap(defaultValue)

    override var configValue: MutableMap<String, List<String>> = map(this.defaultValue)

    private fun map(map: Map<String, List<String>>): NotifiableLinkedHashMap<String, List<String>> {
        val mapValues = map.mapValues { (_, value) ->
            notifiableList(value)
        }
        return NotifiableLinkedHashMap(mapValues).apply {
            subscribe {
                this@ConfigStringListMap.onChange(this@ConfigStringListMap)
            }
        }
    }

    private fun notifiableList(list: List<String>): List<String> {
        return NotifiableArrayList(list).apply {
            subscribe {
                this@ConfigStringListMap.onChange(this@ConfigStringListMap)
            }
        }
    }

    override fun restDefault() {
        if (isDefault()) return
        configValue = map(defaultValue)
        onChange(this)
    }

    override fun serialization(): SerializeElement =
        serializeObject(configValue)

    override fun deserialization(serializeElement: SerializeElement) {
        serializeElement.asObject.apply {
            configValue = this@ConfigStringListMap.map(this.mapValues { (_, value) ->
                notifiableList(value.asArray.map { it.asString })
            })
        }
    }
}