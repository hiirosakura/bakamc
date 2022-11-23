package cn.bakamc.common.api

import cn.bakamc.common.utils.gson

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.common.api

 * 文件名 WSMessage

 * 创建时间 2022/8/30 22:35

 * @author forpleuvoir

 */
data class WSMessage(
	val type: String,
	val data: String = ""
) {
	companion object {
		fun parse(json: String, wsMessage: WSMessage.() -> Unit, onException: (Exception) -> Unit) {
			try {
				gson.fromJson(json, WSMessage::class.java).run {
					wsMessage.invoke(this)
				}
			} catch (e: Exception) {
				onException.invoke(e)
			}
		}

		fun parse(json: String, wsMessage: WSMessage.() -> Unit) {
			parse(json, wsMessage) { it.printStackTrace() }
		}
	}

}

fun String.parseToWSMessage(wsMessage: WSMessage.() -> Unit, onException: (Exception) -> Unit) {
	try {
		gson.fromJson(this, WSMessage::class.java).run {
			wsMessage.invoke(this)
		}
	} catch (e: Exception) {
		onException.invoke(e)
	}
}

fun String.parseToWSMessage(wsMessage: WSMessage.() -> Unit) {
	parseToWSMessage(wsMessage) { it.printStackTrace() }
}