package cn.bakamc.common.config.options.impl

import cn.bakamc.common.config.ConfigType
import cn.bakamc.common.config.ConfigTypes
import cn.bakamc.common.config.options.ConfigBase
import cn.bakamc.common.config.options.IConfigPairs
import cn.bakamc.common.utils.ifc
import cn.bakamc.common.utils.jsonArray
import cn.bakamc.common.utils.jsonObject
import com.google.common.collect.ImmutableList
import com.google.gson.JsonElement

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options.impl

 * 文件名 ConfigPairs

 * 创建时间 2022/7/7 18:32

 * @author forpleuvoir

 */
class ConfigStringPairs(
	override val key: String,
	override val displayName: String,
	override val description: String,
	override val defaultValue: List<Pair<String, String>>,
) : ConfigBase<List<Pair<String, String>>>(), IConfigPairs<String, String> {

	override val type: ConfigType
		get() = ConfigTypes.STRING_PAIRS

	override var configValue: List<Pair<String, String>> = ArrayList(defaultValue)

	private inline fun valueAsMutable(action: MutableList<Pair<String, String>>.() -> Unit) {
		action(configValue as ArrayList<Pair<String, String>>)
	}

	override fun setValue(value: List<Pair<String, String>>) {
		super.setValue(ArrayList(value))
	}

	override fun getValue(): List<Pair<String, String>> = ImmutableList.copyOf(configValue)

	override fun add(key: String, value: String): Boolean {
		valueAsMutable {
			return add(key to value).also { onChanged() }
		}
		return false
	}

	override fun remove(key: String, value: String): Boolean {
		valueAsMutable {
			return remove(key to value).also { onChanged() }
		}
		return false
	}

	override fun remove(index: Int): Pair<String, String>? {
		valueAsMutable {
			return removeAt(index).also { onChanged() }
		}
		return null
	}

	override fun get(key: String): List<String> {
		return ImmutableList.builder<String>().apply {
			configValue.filter { it.first == key }.forEach { add(it.second) }
		}.build()
	}

	override fun get(index: Int): Pair<String, String>? {
		return if (configValue.size > index)
			configValue[index]
		else null
	}

	override fun clear() {
		valueAsMutable { clear() }
	}

	override fun matched(regex: Regex): Boolean {
		getValue().forEach { (regex.containsMatchIn(it.first) || regex.containsMatchIn(it.second)).ifc { return true } }
		return super.matched(regex)
	}

	override fun setFromJson(jsonElement: JsonElement): Boolean {
		if (!jsonElement.isJsonArray) return false
		jsonElement.asJsonArray.apply {
			clear()
			forEach {
				it.asJsonObject.apply { valueAsMutable { add(this[0].first to this[0].second) } }
			}
		}
		return true
	}

	override val serialization: JsonElement
		get() = jsonArray(configValue) {
			jsonObject { it.first at it.second }
		}

}