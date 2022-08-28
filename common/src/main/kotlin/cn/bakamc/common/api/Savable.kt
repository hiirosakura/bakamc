package cn.bakamc.common.api


/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.api

 * 文件名 Savable

 * 创建时间 2022/8/13 0:36

 * @author forpleuvoir

 */
interface Savable {

	fun save()

	fun load()

	fun saveAsync()

	fun loadAsync()

	val needSave: Boolean get() = true
}
