package cn.bakamc.common.config.options

import com.google.gson.JsonElement
import cn.bakamc.common.config.Config
import java.util.concurrent.ConcurrentHashMap

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options

 * 文件名 ConfigBase

 * 创建时间 2022/7/4 13:18

 * @author forpleuvoir

 */
abstract class ConfigBase<T> : Config<T> {

	protected abstract var configValue: T

	override fun setValue(value: T) {
		val oldValue = configValue
		if (value != oldValue) {
			configValue = value
			onChanged()
		}
	}

	override fun init() {}

	private var onChangedCallbacks: MutableMap<Any, (Any) -> Unit> = ConcurrentHashMap()

	override fun getValue(): T = configValue

	override val isDefault: Boolean get() = getValue() == defaultValue

	override fun resetDefValue() = setValue(defaultValue)

	override fun matched(regex: Regex): Boolean {
		return regex.run {
			containsMatchIn(displayName)
					|| containsMatchIn(description)
					|| containsMatchIn(key)
					|| containsMatchIn(getValue().toString())
					|| containsMatchIn(type.type)
		}
	}

	override fun toString(): String {
		return "${type.type}{${key}:${getValue()}}"
	}

	protected abstract fun setFromJson(jsonElement: JsonElement): Boolean

	override fun deserialize(serializedObject: JsonElement) {
		try {
			if (!setFromJson(serializedObject)) {
				println("Failed to set config value '$key' from the JSON element ${serializedObject.asString}")
			}
		} catch (e: Exception) {
			println("Failed to set config value '$key' from the JSON element ${serializedObject.asString}")
			e.printStackTrace()
		}
	}

	override fun subscribeChange(obj: Any, callback: (Any) -> Unit) {
		onChangedCallbacks[obj] = callback
	}

	override fun onChanged() {
		onChangedCallbacks.forEach { (_, v) ->
			v.invoke(getValue() as Any)
		}
	}

	override fun equals(other: Any?): Boolean {
		return if (other is ConfigBase<*>) {
			this.key == other.key && this.getValue() == other.getValue()
		} else false
	}

	override fun hashCode(): Int {
		return key.hashCode() and this.getValue().hashCode()
	}

}