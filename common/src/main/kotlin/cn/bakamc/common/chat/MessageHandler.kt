package cn.bakamc.common.chat

import cn.bakamc.common.chat.config.ChatConfig

/**
 * 消息处理器

 * 项目名 bakamc

 * 包名 cn.bakamc.common.chat

 * 文件名 MessageHandler

 * 创建时间 2022/8/28 17:33

 * @author forpleuvoir

 */
interface MessageHandler {

	/**
	 * 配置
	 */
	val config: ChatConfig

	/**
	 * 服务器信息
	 */
	val serverInfo: ServerInfo get() = config.serverInfo

	/**
	 * 重新加载配置
	 */
	fun reloadConfig()

}