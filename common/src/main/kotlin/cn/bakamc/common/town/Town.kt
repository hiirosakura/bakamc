package cn.bakamc.common.town

import cn.bakamc.common.api.serialization.JsonSerializer
import cn.bakamc.common.common.PlayerInfo
import cn.bakamc.common.utils.jsonArray
import cn.bakamc.common.utils.jsonObject
import com.google.gson.JsonElement
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
) : JsonSerializer {

	companion object {
		val NONE get() = Town(0, "none", "", Date(0), ConcurrentLinkedDeque(), ConcurrentLinkedDeque(), ConcurrentLinkedDeque())

		fun deserialize(serializedObject: JsonElement): Town {
			serializedObject.asJsonObject.apply {
				return Town(
					id = this["id"].asInt,
					name = this["name"].asString,
					shortName = this["short_name"].asString,
					createTime = Date(this["create_time"].asLong),
					mayor = ConcurrentLinkedDeque(this["mayor"].asJsonArray.map { PlayerInfo.deserialize(it) }),
					admin = ConcurrentLinkedDeque(this["admin"].asJsonArray.map { PlayerInfo.deserialize(it) }),
					member = ConcurrentLinkedDeque(this["member"].asJsonArray.map { PlayerInfo.deserialize(it) })
				)
			}
		}
	}

	fun formatTime(format: String = "yyyy-MM-dd HH:mm:ss"): String = SimpleDateFormat(format).format(createTime)

	override val serialization: JsonElement
		get() = jsonObject {
			"id" at id
			"name" at name
			"short_name" at shortName
			"create_time" at createTime.time
			"mayor" at jsonArray(mayor) { it.serialization }
			"admin" at jsonArray(admin) { it.serialization }
			"member" at jsonArray(member) { it.serialization }
		}

	override fun toString(): String {
		return "Town(id=$id, name='$name', shortName='$shortName', createTime=${formatTime()}, mayor=${mayor.map { it.name }}, admin=${admin.map { it.name }}, member=${member.map { it.name }})"
	}


}