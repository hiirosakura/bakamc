package cn.bakamc.common.utils

/**
 * 字符串工具

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.utils

 * 文件名 StringUtil

 * 创建时间 2022/8/14 21:54

 * @author forpleuvoir

 */


/**
 * 如果字符串长度小于[length],则填充字符[fillChar]至长度[length]
 * @receiver [String]
 * @param length [Int]
 * @param fillChar [Char]
 * @param before [Boolean] true:在原字符串前填充,false:在原字符串后填充
 * @return String
 */
fun String.fill(length: Int, fillChar: Char, before: Boolean): String {
	val i = length - this.length
	val sb = StringBuilder()
	if (i > 0) {
		for (j in 0 until i) {
			sb.append(fillChar)
		}
	}
	return if (before) sb.toString() + this else this + sb.toString()
}

/**
 * 如果字符串长度小于[length],则原字符串前填充字符[fillChar]至长度[length]
 * @receiver [String]
 * @param length [Int]
 * @param fillChar [Char]
 * @return String
 */
fun String.fillBefore(length: Int, fillChar: Char): String {
	return fill(length, fillChar, true)
}

/**
 * 如果字符串长度小于[length],则原字符串后填充字符[fillChar]至长度[length]
 * @receiver [String]
 * @param length [Int]
 * @param fillChar [Char]
 * @return String
 */
fun String.fillAfter(length: Int, fillChar: Char): String {
	return fill(length, fillChar, false)
}


fun String.replace(map: Map<String, String>): String {
	var temp: String = this
	map.forEach { (k, v) ->
		temp = temp.replace(k, v)
	}
	return temp
}

fun String.replace(origin: Iterable<String>, new: String): String {
	var temp: String = this
	origin.forEach {
		temp = temp.replace(it, new)
	}
	return temp
}

fun String.replace(origin: Array<String>, new: String): String {
	var temp: String = this
	origin.forEach {
		temp = temp.replace(it, new)
	}
	return temp
}