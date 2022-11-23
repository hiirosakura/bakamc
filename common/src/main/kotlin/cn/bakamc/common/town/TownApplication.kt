package cn.bakamc.common.town

import cn.bakamc.common.api.serialization.JsonSerializer
import cn.bakamc.common.common.PlayerInfo
import cn.bakamc.common.utils.jsonObject
import cn.bakamc.common.utils.toJsonObject
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.common.town

 * 文件名 TownApplication

 * 创建时间 2022/9/12 11:46

 * @author forpleuvoir

 */
class TownApplication(
	val id: Int = 0,
	val townID: Int,
	val applicant: PlayerInfo,
	val message: String,
	val applicationTime: Date
) : JsonSerializer {
	override val serialization: JsonElement
		get() = jsonObject {
			"id" at id
			"town_id" at townID
			"applicant" at applicant
			"message" at message
			"application_time" at applicationTime.time
		}

	companion object {
		fun deserialize(serializedObject: JsonElement): TownApplication {
			serializedObject.asJsonObject.apply {
				return TownApplication(
					this["id"].asInt,
					this["town_id"].asInt,
					PlayerInfo.deserialize(this["applicant"]),
					this["message"].asString,
					Date(this["application_time"].asLong)
				)
			}
		}
	}

	override fun toString(): String {
		return "TownApplication(id=$id, town_id=${townID}, applicant=${applicant.name}, message='$message', applicationTime=$applicationTime)"
	}
}