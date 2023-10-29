package cn.bakamc.folia.config.base

import cn.bakamc.folia.event.entity.BlockInfo
import moe.forpleuvoir.nebula.common.util.NotifiableLinkedHashMap
import moe.forpleuvoir.nebula.config.ConfigBase
import moe.forpleuvoir.nebula.config.item.ConfigMutableMapValue
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject

class ConfigBlockInfos(
    override val key: String,
    defaultValue: Map<String, BlockInfo>
) : ConfigBase<MutableMap<String, BlockInfo>, ConfigBlockInfos>(), ConfigMutableMapValue<String, BlockInfo> {

    override val defaultValue: MutableMap<String, BlockInfo> = LinkedHashMap(defaultValue)

    override var configValue: MutableMap<String, BlockInfo> = map(defaultValue)

    private fun map(map: Map<String, BlockInfo>): NotifiableLinkedHashMap<String, BlockInfo> {
        return NotifiableLinkedHashMap(map).apply {
            subscribe {
                this@ConfigBlockInfos.onChange(this@ConfigBlockInfos)
            }
        }
    }

    override fun restDefault() {
        if (isDefault()) return
        configValue = map(defaultValue)
        onChange(this)
    }

    override fun serialization(): SerializeElement = serializeObject(configValue)

    override fun deserialization(serializeElement: SerializeElement) {
        serializeElement.asObject.apply {
            configValue = this@ConfigBlockInfos.map(this.mapValues { (_, block) ->
                BlockInfo.deserialization(block)
            })
        }
    }

}