package cn.bakamc.common.utils

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.utils

 * 文件名 Misc

 * 创建时间 2022/8/28 15:01

 * @author forpleuvoir

 */

inline fun Boolean?.ifc(action: () -> Unit) {
	if (this == true) {
		action.invoke()
	}
}

inline fun Boolean?.notc(action: () -> Unit) = if (this != null) {
	if (!this) action() else Unit
} else Unit

fun <T> Boolean?.ternary(v1: T, v2: T): T = if (this == true) v1 else v2

