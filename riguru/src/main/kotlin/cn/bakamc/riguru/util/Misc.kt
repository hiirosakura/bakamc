package cn.bakamc.riguru.util

import javax.websocket.Session

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.util

 * 文件名 Misc

 * 创建时间 2022/9/11 14:48

 * @author forpleuvoir

 */

fun Session.sendMessage(message: String) {
	this.basicRemote.sendText(message)
}

fun Iterable<Session>.broadcast(message: String) {
	this.forEach { it.sendMessage(message) }
}
