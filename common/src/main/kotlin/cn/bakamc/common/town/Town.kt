package cn.bakamc.common.town

import cn.bakamc.common.common.PlayerInfo
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.common.town

 * 文件名 Town

 * 创建时间 2022/8/30 17:20

 * @author forpleuvoir

 */
class Town(
	/**
	 * 小镇ID
	 */
	val id: Int,
	/**
	 * 小镇名
	 */
	val name: String,
	/**
	 * 简称
	 */
	val shortName: String,
	/**
	 * 创建时间
	 */
	val createTime: Date,
	/**
	 * 镇长
	 */
	val mayor: ConcurrentLinkedDeque<PlayerInfo>,
	/**
	 * 管理员
	 */
	val admin: ConcurrentLinkedDeque<PlayerInfo>,
	/**
	 * 小镇成员
	 */
	val member: ConcurrentLinkedDeque<PlayerInfo>,
) {

	companion object {
		val NONE get() = Town(0, "none", "", Date(0), ConcurrentLinkedDeque(), ConcurrentLinkedDeque(), ConcurrentLinkedDeque())
	}

	fun formatTime(format: String = "yyyy-MM-dd HH:mm:ss"): String = SimpleDateFormat(format).format(createTime)

	override fun toString(): String {
		return "Town(id=$id, name='$name', shortName='$shortName', createTime=${formatTime()}, mayor=${mayor.map { it.name }}, admin=${admin.map { it.name }}, member=${member.map { it.name }})"
	}


}