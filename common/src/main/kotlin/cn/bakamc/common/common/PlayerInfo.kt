package cn.bakamc.common.common

import cn.bakamc.common.town.Town
import java.util.*

/**
 * 玩家信息

 * 项目名 bakamc

 * 包名 cn.bakamc.common.common

 * 文件名 PlayerInfo

 * 创建时间 2022/8/28 15:55

 * @author forpleuvoir

 */
class PlayerInfo(
	/**
	 * 玩家名称
	 */
	val name: String,
	/**
	 * 玩家显示名称
	 */
	val displayName: String,
	/**
	 * 玩家uuid
	 */
	val uuid: UUID,
	/**
	 * 所在小镇
	 */
	val town: Town,
	/**
	 * 玩家等级
	 */
	val level: Int,
	/**
	 * 玩家经验值
	 */
	val experience: Int,
	/**
	 * 最大生命值
	 */
	val maxHealth: Float,
	/**
	 * 当前生命值
	 */
	val health: Float,
	/**
	 * 所在维度
	 */
	val dimension: String,
) {
	override fun toString(): String {
		return "PlayerInfo(name='$name', displayName='$displayName', uuid=$uuid, town=$town, level=$level, experience=$experience, maxHealth=$maxHealth, health=$health, dimension='$dimension')"
	}
}