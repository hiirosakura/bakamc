package cn.bakamc.common.config.options.impl

import cn.bakamc.common.config.ConfigType
import cn.bakamc.common.config.ConfigTypes
import cn.bakamc.common.config.options.ConfigBase
import cn.bakamc.common.config.options.IConfigColor
import cn.bakamc.common.utils.color.Color
import com.google.gson.JsonElement

/**
 * 颜色配置实现

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options.impl

 * 文件名 ConfigColor

 * 创建时间 2022/7/5 0:29

 * @author forpleuvoir

 */
open class ConfigColor(
	override val key: String,
	override val displayName: String,
	override val description: String,
	final override val defaultValue: Color,
) : ConfigBase<Color>(), IConfigColor {

	override val type: ConfigType
		get() = ConfigTypes.COLOR

	override var configValue: Color = defaultValue.copy()

	override fun setValue(value: Color) {
		if (configValue == value) return
		configValue = value.copy()
		onChanged()
	}

	override fun getValue(): Color = configValue.copy()

	override fun setFromJson(jsonElement: JsonElement): Boolean {
		configValue.deserialize(jsonElement)
		return true
	}

	override val serialization: JsonElement
		get() = getValue().serialization
}