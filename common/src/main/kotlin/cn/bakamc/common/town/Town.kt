package cn.bakamc.common.town

import java.sql.Timestamp
import java.util.UUID
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
	val shortName:String,
	/**
	 * 镇长UUID
	 */
	val mayor: UUID,
	/**
	 * 创建时间
	 */
	val createTime: Timestamp,
	/**
	 * 管理员
	 */
	val administrators: ConcurrentLinkedDeque<UUID>,
	/**
	 * 小镇成员
	 */
	val members: ConcurrentLinkedDeque<UUID>,
) {
}