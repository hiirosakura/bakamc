package cn.bakamc.common.town

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
	 * 镇长UUID
	 */
	val mayor: UUID,
	/**
	 * 创建时间
	 */
	val createTime: Long,
	/**
	 * 管理员
	 */
	val administrators: ConcurrentLinkedDeque<UUID>,
	/**
	 * 小镇成员
	 */
	val members: ConcurrentLinkedDeque<UUID>,
) {

	companion object {
		val NONE get() = Town(0, "none", "", UUID(0L, 0L), 0, ConcurrentLinkedDeque(), ConcurrentLinkedDeque())
	}

	fun formatTime(format: String): String = SimpleDateFormat(format).format(createTime)

	override fun toString(): String {
		return "Town(id=$id, name='$name', shortName='$shortName', mayor=$mayor, createTime=$createTime, administrators=$administrators, members=$members)"
	}


}