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
}