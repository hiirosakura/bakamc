package cn.bakamc.common.common

import java.util.*

/**
 * 玩家信息

 * 项目名 bakamc

 * 包名 cn.bakamc.common.common

 * 文件名 PlayerInfo

 * 创建时间 2022/9/11 23:58

 * @author forpleuvoir

 */
open class PlayerInfo(
	/**
	 * 玩家uuid
	 */
	open val uuid: UUID,
	/**
	 * 玩家名
	 */
	open val name: String,
	/**
	 * 玩家显示名称
	 */
	open val displayName: String,
) {
	override fun toString(): String {
		return "PlayerInfo(uuid=$uuid, name='$name', displayName='$displayName')"
	}

	fun uuid(): String = uuid.toString()

	companion object {
		val NONE get() = PlayerInfo(UUID(0, 0), "", "")
	}
}