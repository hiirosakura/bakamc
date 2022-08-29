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

	const val PLACEHOLDER = "&"

	val map = mapOf(
		"${PLACEHOLDER}4" to "§4",
		"${PLACEHOLDER}c" to "§c",
		"${PLACEHOLDER}6" to "§6",
		"${PLACEHOLDER}e" to "§e",
		"${PLACEHOLDER}2" to "§2",
		"${PLACEHOLDER}a" to "§a",
		"${PLACEHOLDER}b" to "§b",
		"${PLACEHOLDER}3" to "§3",
		"${PLACEHOLDER}1" to "§1",
		"${PLACEHOLDER}9" to "§9",
		"${PLACEHOLDER}d" to "§d",
		"${PLACEHOLDER}5" to "§5",
		"${PLACEHOLDER}f" to "§f",
		"${PLACEHOLDER}7" to "§7",
		"${PLACEHOLDER}8" to "§8",
		"${PLACEHOLDER}0" to "§0",
		"${PLACEHOLDER}k" to "§k",
		"${PLACEHOLDER}l" to "§l",
		"${PLACEHOLDER}m" to "§m",
		"${PLACEHOLDER}n" to "§n",
		"${PLACEHOLDER}o" to "§o",
		"${PLACEHOLDER}r" to "§r",
	)

	/**
	 * 处理消息格式
	 * @param message [String] 原始消息 &6message
	 * @return [String] 处理后的消息 §6message
	 */
	fun handleFormat(message: String, map: Map<String, String>): String {
		return message.replace(map)
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
}