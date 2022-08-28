package cn.bakamc.common.config.options.impl

import cn.bakamc.common.config.ConfigType
import cn.bakamc.common.config.ConfigTypes
import cn.bakamc.common.config.options.ConfigBase
import cn.bakamc.common.config.options.IConfigList
import cn.bakamc.common.utils.ifc
import cn.bakamc.common.utils.jsonArray
import com.google.common.collect.ImmutableList
import com.google.gson.JsonElement

/**
 * 字符串列表

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options.impl

 * 文件名 ConfigStringList

 * 创建时间 2022/7/6 13:20

 * @author forpleuvoir

 */
open class ConfigStringList(
	override val key: String,
	override val displayName: String,
	override val description: String,
	final override val defaultValue: List<String>,
) : ConfigBase<List<String>>(), IConfigList<String> {

	override val type: ConfigType
		get() = ConfigTypes.STRING_LIST

	override var configValue: List<String> = ArrayList(defaultValue)

	private inline fun valueAsMutable(action: MutableList<String>.() -> Unit) {
		action(configValue as ArrayList<String>)
	}

	override fun setValue(value: List<String>) {
		if (this.configValue != value) {
			valueAsMutable {
				this.clear()
				this.addAll(value)
			}
			this.onChanged()
		}
	}

	override fun getValue(): List<String> = ImmutableList.copyOf(configValue)

	override fun add(element: String): String? {
		valueAsMutable {
			if (add(element)) {
				onChanged()
				return element
			}
		}
		return null
	}

	override fun get(index: Int): String? = configValue[index]

	override fun set(index: Int, element: String) {
		valueAsMutable {
			if (this[index] != element) {
				this[index] = element
				onChanged()
			}
		}
	}

	override fun remove(index: Int): String? {
		valueAsMutable {
			if (index >= 0 && index < this.size) {
				onChanged()
				return this.removeAt(index)
			}
		}
		return null
	}

	override fun remove(element: String): String? {
		valueAsMutable {
			if (remove(element)) {
				onChanged()
				return element
			}
		}
		return null
	}

	override fun clear() {
		valueAsMutable { clear() }
	}

	override fun matched(regex: Regex): Boolean {
		getValue().forEach { regex.containsMatchIn(it).ifc { return true } }
		return super.matched(regex)
	}

	override fun setFromJson(jsonElement: JsonElement): Boolean {
		if (!jsonElement.isJsonArray) return false
		valueAsMutable {
			val jsonArray = jsonElement.asJsonArray
			clear()
			jsonArray.forEach { add(it.asString) }
		}
		return true
	}

	override val serialization: JsonElement
		get() = jsonArray(getValue())
}