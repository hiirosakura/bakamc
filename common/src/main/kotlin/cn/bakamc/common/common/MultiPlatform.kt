package cn.bakamc.common.common

import cn.bakamc.common.town.Town

/**
 * 多平台方法

 * 项目名 bakamc

 * 包名 cn.bakamc.common.common

 * 文件名 MultiPlatform

 * 创建时间 2022/11/16 15:31

 * @author forpleuvoir

 */
interface MultiPlatform<T, P, S> {

	enum class ClickAction(val id: String) {
		OPEN_URL("open_url"),
		OPEN_FILE("open_file"),
		RUN_COMMAND("run_command"),
		SUGGEST_COMMAND("suggest_command"),
		CHANGE_PAGE("change_page"),
		COPY_TO_CLIPBOARD("copy_to_clipboard")
	}

	enum class HoverAction(val id: String) {
		SHOW_TEXT("show_text"),
		SHOW_ITEM("show_item"),
		SHOW_ENTITY("show_entity")
	}

	/**
	 * 在当前文本后添加文本
	 * @receiver T
	 * @param text T
	 * @return T
	 */
	fun addSiblings(origin: T, sibling: T): T

	/**
	 * 为文本添加点击事件
	 * @param text T
	 * @param action ClickAction
	 * @param value String
	 * @return T
	 */
	fun withClick(text: T, action: ClickAction, value: String): T

	/**
	 * 为文本添加悬浮事件
	 * @param text T
	 * @param action HoverAction
	 * @param content Any
	 * @return T
	 */
	fun withHover(text: T, action: HoverAction, content: Any): T

	/**
	 * 将Text转换为可解析的Json文本
	 * @receiver T
	 * @return String
	 */
	fun textToJson(text: T): String

	/**
	 * 将Text转换为纯字符串
	 * @param text T
	 * @return String
	 */
	fun textToPlainString(text: T): String

	/**
	 * 将Json文本解析为当前环境的Text
	 * @receiver String
	 * @return T
	 */
	fun textFromJson(json: String): T

	/**
	 * 将字符串转换为当前环境下的Text
	 */
	fun stringToText(str: String): T

	/**
	 * 获取玩家当前信息
	 */
	fun playerCurrentInfo(player: P): PlayerCurrentInfo

	fun playerInfo(player: P): PlayerInfo

	/**
	 * 将玩家转换为对应环境的Text
	 */
	fun playerNameText(playerCurrentInfo: PlayerCurrentInfo, origin: String = "playerName"): T

	/**
	 * 将玩家转换为对应环境的Text 且以[PlayerCurrentInfo.displayName]为显示文本
	 */
	fun playerDisplayNameText(playerCurrentInfo: PlayerCurrentInfo, origin: String = "playerDisplayName"): T

	/**
	 * 将小镇信息转换为对应环境的Text
	 */
	fun townNameText(town: Town, origin: String = "townName"): T

	fun townShortNameText(town: Town, origin: String = "townShortName"): T

	/**
	 * 将服务器信息转换为对应环境的Text
	 */
	fun serverNameText(serverInfo: ServerInfo, origin: String = "serverName"): T

	/**
	 * 将服务器信息转换为对应环境的Text
	 */
	fun serverIdText(serverInfo: ServerInfo, origin: String = "serverID"): T

	/**
	 * 获取当前服务器所有在线的玩家
	 */
	fun players(server: S): Iterable<P>


}