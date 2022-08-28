package cn.bakamc.common.config.options.impl

import cn.bakamc.common.config.ConfigType
import cn.bakamc.common.config.ConfigTypes
import cn.bakamc.common.config.options.ConfigBase
import cn.bakamc.common.config.options.IConfigMap
import cn.bakamc.common.utils.ifc
import cn.bakamc.common.utils.toJsonObject
import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement

/**
 * 字符串映射

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options.impl

 * 文件名 ConfigStringMap

 * 创建时间 2022/7/6 12:55

 * @author forpleuvoir

 */
class ConfigStringMap(
	override val key: String,
	override val displayName: String,
	override val description: String,
	override val defaultValue: Map<String, String>,
) : ConfigBase<Map<String, String>>(), IConfigMap<String, String> {

	override val type: ConfigType
		get() = ConfigTypes.STRING_MAP

	override var configValue: Map<String, String> = LinkedHashMap(defaultValue)

	override fun setValue(value: Map<String, String>) {
		if (this.configValue != value) {
			(configValue as LinkedHashMap).run {
				this.clear()
				this.putAll(value)
				onChanged()
			}
		}
	}

	override fun getValue(): Map<String, String> {
		return ImmutableMap.copyOf(configValue)
	}

	override fun resetDefValue() {
		setValue(LinkedHashMap(defaultValue))
	}

	override fun set(key: String, value: String) {
		val oldValue: String? = this.configValue[key]
		(this.configValue as LinkedHashMap)[key] = value
		if (oldValue != value) {
			this.onChanged()
		}
	}

	override fun get(key: String): String? {
		return configValue[key]
	}

	override fun remove(key: String): String? {
		if (key.contains(key)) {
			val value = (this.configValue as LinkedHashMap).remove(key)
			this.onChanged()
			return value
		}
		return null
	}

	override fun rename(origin: String, current: String) {
		if (this.configValue.containsKey(origin)) {
			val value = this.configValue[origin]
			value?.let {
				(this.configValue as LinkedHashMap).remove(origin)
				(this.configValue as LinkedHashMap)[current] = it
				this.onChanged()
			}
		}
	}

	override fun rest(originKey: String, currentKey: String, value: String) {
		if (this.configValue.containsKey(originKey)) {
			if (originKey != currentKey) rename(originKey, currentKey)
			(this.configValue as LinkedHashMap)[currentKey] = value
			this.onChanged()
		}
	}

	override fun clear() {
		(this.configValue as LinkedHashMap).clear()
	}

	override fun matched(regex: Regex): Boolean {
		getValue().forEach { (k, v) -> (regex.containsMatchIn(k) || regex.containsMatchIn(v)).ifc { return true } }
		return super.matched(regex)
	}

	override fun setFromJson(jsonElement: JsonElement): Boolean {
		if (!jsonElement.isJsonObject) return false
		(configValue as LinkedHashMap).run {
			this.clear()
			jsonElement.asJsonObject.entrySet().forEach {
				this[it.key] = it.value.asString
			}
		}
		return true
	}

	override val serialization: JsonElement
		get() = getValue().toJsonObject()

}