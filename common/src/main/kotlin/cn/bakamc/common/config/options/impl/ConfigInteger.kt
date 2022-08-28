package cn.bakamc.common.config.options.impl

import cn.bakamc.common.config.ConfigType
import cn.bakamc.common.config.ConfigTypes
import cn.bakamc.common.config.options.ConfigBase
import cn.bakamc.common.config.options.IConfigInteger
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

/**
 * 整数配置实现

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options.impl

 * 文件名 ConfigInteger

 * 创建时间 2022/7/5 13:25

 * @author forpleuvoir

 */
open class ConfigInteger(
	override val key: String,
	override val displayName: String,
	override val description: String,
	final override val defaultValue: Int,
	override val minValue: Int = Int.MIN_VALUE,
	override val maxValue: Int = Int.MAX_VALUE,
) : ConfigBase<Int>(), IConfigInteger {

	override val type: ConfigType
		get() = ConfigTypes.INTEGER

	override var configValue: Int = defaultValue

	override fun setValue(value: Int) {
		super.setValue(fixValue(value))
	}

	override fun setFromJson(jsonElement: JsonElement): Boolean {
		if (!jsonElement.isJsonPrimitive) return false
		this.configValue = jsonElement.asInt
		return true
	}

	override val serialization: JsonElement
		get() = JsonPrimitive(getValue())

}