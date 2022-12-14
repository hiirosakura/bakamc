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
		 * 同步所有小镇数据
		 */
		const val TOWN_SYNC_ALL_DATA = "town_sync_all_data"
	}

	object Player {
		/**
		 * 玩家加入某个服务器
		 */
		const val PLAYER_JOIN = "player_join"

		/**
		 * 玩家离开某个服务器
		 */
		const val PLAYER_LEFT = "player_left"

		/**
		 * 同步玩家数据
		 */
		const val PLAYER_SYNC_ALL_DATA = "player_sync_all_data"
	}
}