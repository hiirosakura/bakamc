package cn.bakamc.common.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

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


/**
 * 格式化字符串列表 类似
 * {str1,str2,str3,str4}
 * @receiver Iterable<String>
 * @param length Long
 * @param ellipsis String
 * @param separator String
 * @param prefix String
 * @param suffix String
 * @return String
 */
fun Iterable<String>.format(length: Long, ellipsis: String = "...", separator: String = ", ", prefix: String = "", suffix: String = ""): String {
	val sb = StringBuffer(prefix)
	for ((index, s) in this.withIndex()) {
		if (index > length) {
			sb.append(ellipsis)
			break
		}
		sb.append(s)
		if (this.last() != s) sb.append(separator)
	}
	sb.append(suffix)
	return sb.toString()
}

fun LocalDateTime.toDate(): Date {
	return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}

fun Date.toLocalDateTime(): LocalDateTime {
	return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
}