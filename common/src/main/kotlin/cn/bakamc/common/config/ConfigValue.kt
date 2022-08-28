package cn.bakamc.common.config

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config

 * 文件名 ConfigValue

 * 创建时间 2022/7/4 0:15

 * @author forpleuvoir

 */
interface ConfigValue<T> {

	val defaultValue: T

	fun setValue(value: T)

	fun getValue(): T

}