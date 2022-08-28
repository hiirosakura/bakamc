package cn.bakamc.common.config.options.impl

import cn.bakamc.common.config.ConfigType
import cn.bakamc.common.config.ConfigTypes
import cn.bakamc.common.config.options.ConfigBase
import cn.bakamc.common.config.options.IConfigBoolean
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

/**
 * 布尔配置实现

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options.impl

 * 文件名 ConfigBoolean

 * 创建时间 2022/7/5 0:19

 * @author forpleuvoir

 */
open class ConfigBoolean(
	override val key: String,
	override val displayName: String,
	override val description: String,
	final override val defaultValue: Boolean,
) : ConfigBase<Boolean>(), IConfigBoolean {

	override val type: ConfigType
		get() = ConfigTypes.BOOLEAN

	override var configValue: Boolean = defaultValue

	override fun setFromJson(jsonElement: JsonElement): Boolean {
		if (!jsonElement.isJsonPrimitive) return false
		this.configValue = jsonElement.asBoolean
		return true
	}

	override val serialization: JsonElement
		get() = JsonPrimitive(getValue())


}