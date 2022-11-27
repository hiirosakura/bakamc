package cn.bakamc.common.player

import cn.bakamc.common.api.serialization.JsonSerializer
import cn.bakamc.common.utils.jsonObject
import com.google.gson.JsonElement
import java.util.*

/**
 * 玩家信息

 * 项目名 bakamc

 * 包名 cn.bakamc.common.player

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

	override fun toString(): String {
		return "PlayerInfo(uuid=$uuid, name='$name', displayName='$displayName')"
	}

	fun uuid(): String = uuid.toString()
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as PlayerInfo

		if (uuid != other.uuid) return false

		return true
	}

	override fun hashCode(): Int {
		return uuid.hashCode()
	}


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