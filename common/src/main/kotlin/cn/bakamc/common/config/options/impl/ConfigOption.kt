package cn.bakamc.common.config.options.impl

import cn.bakamc.common.api.Option
import cn.bakamc.common.config.ConfigType
import cn.bakamc.common.config.ConfigTypes
import cn.bakamc.common.config.options.ConfigBase
import cn.bakamc.common.config.options.IConfigOption
import cn.bakamc.common.utils.ifc
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

/**
 * 选项配置实现

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options.impl

 * 文件名 ConfigOption

 * 创建时间 2022/7/7 12:43

 * @author forpleuvoir

 */
open class ConfigOption(
	override val key: String,
	override val displayName: String,
	override val description: String,
	private val options: LinkedHashSet<Option>,
	final override val defaultValue: Option = options.first(),
) : ConfigBase<Option>(), IConfigOption {

	override val type: ConfigType
		get() = ConfigTypes.OPTIONS

	override var configValue: Option = defaultValue

	override fun setValue(value: Option) {
		if (options.contains(value))
			super.setValue(value)
	}

	override fun getOptions(): LinkedHashSet<Option> = options

	override fun switch() {
		val index = options.indexOf(configValue)
		val size = options.size
		if (index < size - 1) {
			setValue(options.toTypedArray()[index + 1])
		} else {
			setValue(options.toTypedArray()[0])
		}
	}

	override fun matched(regex: Regex): Boolean {
		options.forEach {
			(regex.containsMatchIn(it.key) || regex.containsMatchIn(it.description) || regex.containsMatchIn(it.displayName)).ifc { return true }
		}
		return super.matched(regex)
	}

	override fun setFromJson(jsonElement: JsonElement): Boolean {
		if (!jsonElement.isJsonPrimitive) return false
		jsonElement.asJsonPrimitive.apply {
			options.find { it.key == this.asString }?.let {
				configValue = it
				return true
			}
		}
		return false
	}

	override val serialization: JsonElement
		get() = JsonPrimitive(configValue.key)


}