package cn.bakamc.folia.config.base

import moe.forpleuvoir.nebula.common.util.NotifiableLinkedHashMap
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.config.item.ConfigMutableMapValue
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject

class ConfigStringDoubleMap(
    override val key: String,
    defaultValue: Map<String, Double>
) : ConfigBase<MutableMap<String, Double>, ConfigStringDoubleMap>(), ConfigMutableMapValue<String, Double> {

    override val defaultValue: MutableMap<String, Double> = LinkedHashMap(defaultValue)

    override var configValue: MutableMap<String, Double> = map(this.defaultValue)

    private fun map(map: Map<String, Double>): NotifiableLinkedHashMap<String, Double> {
        return NotifiableLinkedHashMap(map).apply {
            subscribe {
                this@ConfigStringDoubleMap.onChange(this@ConfigStringDoubleMap)
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
            configValue = this@ConfigStringDoubleMap.map(this.mapValues {
                it.value.asDouble
            })
        }
    }
}