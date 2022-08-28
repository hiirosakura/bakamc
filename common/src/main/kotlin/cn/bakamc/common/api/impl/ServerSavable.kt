package cn.bakamc.common.api.impl

import cn.bakamc.common.api.Savable


/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.api.impl

 * 文件名 ServerSavable

 * 创建时间 2022/8/12 13:43

 * @author forpleuvoir

 */
interface ServerSavable<S> : Savable {

	var server: S

	fun init(server: S)

}