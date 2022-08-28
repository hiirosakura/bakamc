package cn.bakamc.common.config.options

import cn.bakamc.common.config.ConfigValue
import cn.bakamc.common.utils.clamp

/**
 * 整数配置

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options

 * 文件名 IConfigInteger

 * 创建时间 2022/7/4 20:31

 * @author forpleuvoir

 */
interface IConfigInteger : ConfigValue<Int> {
	/**
	 * 最小值
	 */
	val minValue: Int get() = Int.MIN_VALUE

	/**
	 * 最大值
	 */
	val maxValue: Int get() = Int.MAX_VALUE

	fun fixValue(value: Int): Int {
		return value.clamp(minValue, maxValue)
	}

}