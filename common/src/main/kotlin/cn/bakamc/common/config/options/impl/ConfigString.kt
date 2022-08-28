package cn.bakamc.common.config.options.impl

import cn.bakamc.common.config.ConfigType
import cn.bakamc.common.config.ConfigTypes
import cn.bakamc.common.config.options.ConfigBase
import cn.bakamc.common.config.options.IConfigString
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

/**
 * 字符串配置实现

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options.impl

 * 文件名 ConfigString

 * 创建时间 2022/7/8 13:07

 * @author forpleuvoir

 */
class ConfigString(
	override val key: String,
	override val displayName: String,
	override val description: String,
	override val defaultValue: String,
) : ConfigBase<String>(), IConfigString {

	override val type: ConfigType
		get() = ConfigTypes.STRING

	override var configValue: String = defaultValue

	override fun setFromJson(jsonElement: JsonElement): Boolean {
		if (!jsonElement.isJsonPrimitive) return false
		configValue = jsonElement.asJsonPrimitive.asString
		return true
	}

	override val serialization: JsonElement
		get() = JsonPrimitive(getValue())
}