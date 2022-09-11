package cn.bakamc.common.api

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.common.api

 * 文件名 WSMessageType

 * 创建时间 2022/8/30 22:38

 * @author forpleuvoir

 */
object WSMessageType {

	object Chat {
		/**
		 * 用于获取配置
		 */
		const val CONFIG = "config"

		/**
		 * 注册服务器信息
		 */
		const val REGISTRY_SERVER_INFO = "registry_server_info"

		/**
		 * 聊天消息
		 */
		const val CHAT_MESSAGE = "chat_message"

		/**
		 * 私聊消息
		 */
		const val WHISPER_MESSAGE = "whisper_message"
	}

	object Town {
		/**
		 * 同步数据
		 */
		const val SYNC_DATA = "sync_data"
	}


}