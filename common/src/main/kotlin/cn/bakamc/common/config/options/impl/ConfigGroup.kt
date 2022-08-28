package cn.bakamc.common.config.options.impl

import cn.bakamc.common.config.Config
import cn.bakamc.common.config.ConfigType
import cn.bakamc.common.config.ConfigTypes
import cn.bakamc.common.config.options.ConfigBase
import cn.bakamc.common.config.options.IConfigGroup
import cn.bakamc.common.utils.ifc
import cn.bakamc.common.utils.jsonObject
import com.google.common.collect.ImmutableSet
import com.google.gson.JsonElement

/**
 * 配置组 配置

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options.impl

 * 文件名 ConfigGroup

 * 创建时间 2022/7/6 0:43

 * @author forpleuvoir

 */
open class ConfigGroup(
	override val key: String,
	override val displayName: String,
	override val description: String,
	final override val defaultValue: Set<Config<*>>,
) : ConfigBase<Set<Config<*>>>(), IConfigGroup {

	override val type: ConfigType
		get() = ConfigTypes.GROUP

	override var configValue: Set<Config<*>> = ImmutableSet.copyOf(defaultValue)

	override fun init() {
		getValue().forEach { it.init() }
	}

	override fun resetDefValue() {
		setValue(ImmutableSet.copyOf(defaultValue))
	}

	override fun getConfigFromKey(key: String): Config<*>? {
		return getValue().find { it.key == key }
	}

	override fun getKeys(): Set<String> {
		return ImmutableSet.Builder<String>().apply { getValue().forEach { add(it.key) } }.build()
	}

	override fun matched(regex: Regex): Boolean {
		getValue().forEach { it.matched(regex).ifc { return true } }
		return super.matched(regex)
	}

	override fun subscribeChange(obj: Any, callback: (Any) -> Unit) {
		getValue().forEach {
			it.subscribeChange(obj, callback)
		}
	}

	override fun setFromJson(jsonElement: JsonElement): Boolean {
		if (!jsonElement.isJsonObject) return false
		val jsonObject = jsonElement.asJsonObject
		this.configValue.forEach {
			it.deserialize(jsonObject[it.key])
		}
		return true
	}

	override val serialization: JsonElement
		get() = jsonObject {
			getValue().forEach { it.key at it.serialization }
		}


}