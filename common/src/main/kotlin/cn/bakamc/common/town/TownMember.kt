package cn.bakamc.common.town

import cn.bakamc.common.common.PlayerInfo

/**
 * 小镇镇民

 * 项目名 bakamc

 * 包名 cn.bakamc.common.town

 * 文件名 TownMember

 * 创建时间 2022/10/21 11:11

 * @author forpleuvoir

 */
class TownMember(
	val playerInfo: PlayerInfo,
	val townID: Int,
	val role: String
)