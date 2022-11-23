package cn.bakamc.common.utils

/**
 * 消息处理工具

 * 项目名 bakamc

 * 包名 cn.bakamc.common.utils

 * 文件名 MessageUtil

 * 创建时间 2022/8/28 17:25

 * @author forpleuvoir

 */
object MessageUtil {

	val defaultMap: Map<String, String> = mapOf(
		"&0" to "§0",
		"&1" to "§1",
		"&2" to "§2",
		"&3" to "§3",
		"&4" to "§4",
		"&5" to "§5",
		"&6" to "§6",
		"&7" to "§7",
		"&8" to "§8",
		"&9" to "§9",
		"&a" to "§a",
		"&b" to "§b",
		"&c" to "§c",
		"&d" to "§d",
		"&e" to "§e",
		"&f" to "§f",
		"&g" to "§g",
		"&h" to "§h",
		"&u" to "§u",
		"&l" to "§l",
		"&o" to "§o",
		"&m" to "§m",
		"&k" to "§k",
		"&r" to "§r"
	)


	/**
	 * 处理消息格式
	 * @param message [String] 原始消息 &6message
	 * @return [String] 处理后的消息 §6message
	 */
	fun handleFormat(message: String, map: Map<String, String>): String {
		var temp = message
		map.forEach { (k, v) ->
			temp = temp.replace(Regex(k), v)
		}
		return temp
	}

	/**
	 * 反向处理消息格式
	 * @param message [String] 原始消息 §6message
	 * @return [String] 处理后的消息 &6message
	 */
	fun reHandleFormat(message: String, map: Map<String, String>): String {
		var temp: String = message
		map.forEach { (k, v) ->
			temp = temp.replace(v, k)
		}
		return temp
	}

	/**
	 * 清除格式符号
	 * @param message String
	 * @return String
	 */
	fun cleanFormatting(message: String, map: Map<String, String>): String {
		var temp: String = message
		map.forEach { (k, v) ->
			temp = temp.replace(k, "")
			temp = temp.replace(v, "")
		}
		return temp
	}

	/**
	 * 清除格式符号
	 * @param message String
	 * @return String
	 */
	fun cleanFormatting(message: String, placeholder: Iterable<String> = defaultMap.values): String {
		var temp: String = message
		placeholder.forEach {
			temp = temp.replace(it, "")
		}
		return temp
	}
}