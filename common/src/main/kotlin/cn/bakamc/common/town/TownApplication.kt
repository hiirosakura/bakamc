package cn.bakamc.common.town

import cn.bakamc.common.common.PlayerInfo
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
) {

	override fun toString(): String {
		return "TownApplication(id=$id, town_id=${townID}, applicant=${applicant.name}, message='$message', applicationTime=$applicationTime)"
	}
}