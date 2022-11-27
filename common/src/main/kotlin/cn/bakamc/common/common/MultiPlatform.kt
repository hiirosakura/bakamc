package cn.bakamc.common.common

import cn.bakamc.common.player.PlayerCurrentInfo
import cn.bakamc.common.player.PlayerInfo
import cn.bakamc.common.town.Town
import java.util.*

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
	fun T.addSibling(sibling: T): T

	/**
	 * 为文本添加点击事件
	 * @param action ClickAction
	 * @param content String
	 * @return T
	 */
	fun T.withClick(action: ClickAction, content: String): T

	/**
	 * 为文本添加悬浮事件
	 * @param action HoverAction
	 * @param content Any 注意参数类型 必须和[action]的参数类型一致
	 * @return T
	 */
	fun T.withHover(action: HoverAction, content: Any): T

	/**
	 * 将Text转换为可解析的Json文本
	 * @receiver T
	 * @return String
	 */
	fun T.toJson(): String

	/**
	 * 将Text转换为纯字符串
	 * @param text T
	 * @return String
	 */
	fun T.toPlain(): String

	/**
	 * 将Json文本解析为当前环境的Text
	 * @receiver String
	 * @return T
	 */
	fun String.fromJson(): T

	/**
	 * 将字符串转换为当前环境下的Text
	 */
	fun String.toText(): T

	/**
	 * 获取玩家当前信息
	 */
	fun P.playerCurrentInfo(): PlayerCurrentInfo

	fun P.playerInfo(): PlayerInfo

	/**
	 * 将玩家转换为对应环境的Text
	 */
	fun PlayerCurrentInfo.nameText(origin: String = "playerName"): T

	/**
	 * 将玩家转换为对应环境的Text 且以[PlayerCurrentInfo.displayName]为显示文本
	 */
	fun PlayerCurrentInfo.displayNameText(origin: String = "playerDisplayName"): T

	/**
	 * 将小镇信息转换为对应环境的Text
	 */
	fun Town.nameText(origin: String = "townName"): T

	fun Town.shortNameText(origin: String = "townShortName"): T

	/**
	 * 将服务器信息转换为对应环境的Text
	 */
	fun ServerInfo.nameText(origin: String = "serverName"): T

	/**
	 * 将服务器信息转换为对应环境的Text
	 */
	fun ServerInfo.idText(origin: String = "serverID"): T

	/**
	 * 获取当前服务器所有在线的玩家
	 */
	fun S.players(): Iterable<P>

	/**
	 * 向当前服务器指定玩家发送消息
	 * @receiver P
	 * @param message Text[T]
	 */
	fun P.sendMessage(message: T, uuid: UUID)

}