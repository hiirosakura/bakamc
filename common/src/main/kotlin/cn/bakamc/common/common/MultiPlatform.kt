package cn.bakamc.common.common

/**
 * 多平台方法

 * 项目名 bakamc

 * 包名 cn.bakamc.common.common

 * 文件名 MultiPlatform

 * 创建时间 2022/11/16 15:31

 * @author forpleuvoir

 */
interface MultiPlatform<T, P, S> {

	/**
	 * 在当前文本后添加文本
	 * @receiver T
	 * @param text T
	 * @return T
	 */
	fun addSiblings(origin: T, vararg sibling: T): T

	/**
	 * 将Text转换为可解析的Json文本
	 * @receiver T
	 * @return String
	 */
	fun toJson(text: T): String

	/**
	 * 将Json文本解析为当前环境的Text
	 * @receiver String
	 * @return T
	 */
	fun fromJson(json: String): T

	/**
	 * 将字符串转换为当前环境下的Text
	 */
	fun toText(str: String): T

	/**
	 * 获取玩家当前信息
	 */
	fun info(player: P): PlayerCurrentInfo

	/**
	 * 将玩家转换为对应环境的Text
	 */
	fun text(playerCurrentInfo: PlayerCurrentInfo, origin: String): T

	/**
	 * 将玩家转换为对应环境的Text 且以[PlayerCurrentInfo.displayName]为显示文本
	 */
	fun displayNameText(playerCurrentInfo: PlayerCurrentInfo, origin: String): T

	/**
	 * 将玩家小镇信息转换为对应环境的Text
	 */
	fun townNameText(playerCurrentInfo: PlayerCurrentInfo, origin: String): T

	fun townShortNameText(playerCurrentInfo: PlayerCurrentInfo, origin: String): T

	/**
	 * 将服务器信息转换为对应环境的Text
	 */
	fun text(serverInfo: ServerInfo, origin: String): T

	/**
	 * 将服务器信息转换为对应环境的Text
	 */
	fun idText(serverInfo: ServerInfo, origin: String): T

	/**
	 * 获取当前服务器所有在线的玩家
	 */
	fun players(server:S): Iterable<P>
}