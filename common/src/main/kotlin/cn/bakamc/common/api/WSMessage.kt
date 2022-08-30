package cn.bakamc.common.api

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
	val data: String
)