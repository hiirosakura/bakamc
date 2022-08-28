package cn.bakamc.common.config.options.impl

import cn.bakamc.common.config.ConfigType
import cn.bakamc.common.config.ConfigTypes
import cn.bakamc.common.config.options.ConfigBase
import cn.bakamc.common.config.options.IConfigDouble
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

/**
 * 浮点配置实现

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options.impl

 * 文件名 ConfigDouble

 * 创建时间 2022/7/5 0:39

 * @author forpleuvoir

 */
open class ConfigDouble(
	override val key: String,
	override val displayName: String,
	override val description: String,
	final override val defaultValue: Double,
	override val minValue: Double = Double.MIN_VALUE,
	override val maxValue: Double = Double.MAX_VALUE,
) : ConfigBase<Double>(), IConfigDouble {

	override val type: ConfigType
		get() = ConfigTypes.DOUBLE

	override var configValue: Double = defaultValue

	override fun setValue(value: Double) {
		super.setValue(fixValue(value))
	}

	override fun setFromJson(jsonElement: JsonElement): Boolean {
		if (!jsonElement.isJsonPrimitive) return false
		this.configValue = jsonElement.asDouble
		return true
	}

	override val serialization: JsonElement
		get() = JsonPrimitive(getValue())

}