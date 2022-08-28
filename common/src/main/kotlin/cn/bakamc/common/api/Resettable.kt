package cn.bakamc.common.api

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.api

 * 文件名 Resettable

 * 创建时间 2022/7/4 0:16

 * @author forpleuvoir

 */
interface Resettable {

	val isDefault: Boolean

	fun resetDefValue()

}