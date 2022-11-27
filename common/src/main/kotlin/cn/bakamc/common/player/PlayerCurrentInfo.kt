package cn.bakamc.common.player

import cn.bakamc.common.town.Town
import java.util.*

/**
 * 玩家当前信息

 * 项目名 bakamc

 * 包名 cn.bakamc.common.player

 * 文件名 PlayerInfo

 * 创建时间 2022/8/28 15:55

 * @author forpleuvoir

 */
class PlayerCurrentInfo(
	/**
	 * 玩家uuid
	 */
	uuid: UUID,
	/**
	 * 玩家名称
	 */
	name: String,
	/**
	 * 玩家显示名称
	 */
	displayName: String,
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
) : PlayerInfo(uuid, name, displayName) {
	override fun toString(): String {
		return "PlayerCurrentInfo(town=$town, level=$level, experience=$experience, maxHealth=$maxHealth, health=$health, dimension='$dimension')"
	}
}