package cn.bakamc.common.config.options

import cn.bakamc.common.config.ConfigValue
import cn.bakamc.common.utils.clamp

/**
 * 浮点配置

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options

 * 文件名 IConfigDouble

 * 创建时间 2022/7/4 20:35

 * @author forpleuvoir

 */
interface IConfigDouble : ConfigValue<Double> {
	/**
	 * 最小值
	 */
	val minValue: Double get() = Double.MIN_VALUE

	/**
	 * 最大值
	 */
	val maxValue: Double get() = Double.MAX_VALUE

	fun fixValue(value: Double): Double {
		return value.clamp(minValue, maxValue)
	}
}