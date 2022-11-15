package cn.bakamc.common.common

import cn.bakamc.common.api.serialization.JsonSerializer
import cn.bakamc.common.utils.jsonObject
import com.google.gson.JsonElement
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
) : JsonSerializer {
	override val serialization: JsonElement
		get() = jsonObject {
			"uuid" at uuid.toString()
			"name" at name
			"display_name" at displayName
		}

	override fun deserialize(serializedObject: JsonElement) {
		TODO("Not yet implemented")
	}

	override fun toString(): String {
		return "PlayerInfo(uuid=$uuid, name='$name', displayName='$displayName')"
	}

	fun uuid(): String = uuid.toString()

	companion object {
		val NONE get() = PlayerInfo(UUID(0, 0), "", "")

		fun deserialize(serializedObject: JsonElement): PlayerInfo {
			serializedObject.asJsonObject.apply {
				return PlayerInfo(
					UUID.fromString(this["uuid"].asString),
					this["name"].asString,
					this["display_name"].asString
				)
			}
		}
	}
}