package cn.bakamc.riguru.mapper

import cn.bakamc.common.common.PlayerInfo
import cn.bakamc.common.town.TownApplication
import cn.bakamc.common.utils.toDate
import cn.bakamc.riguru.entity.TownApplicationVO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import java.time.LocalDateTime
import java.util.*

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.mapper

 * 文件名 TownApplicationMapper

 * 创建时间 2022/9/12 11:50

 * @author forpleuvoir

 */
@Mapper
interface TownApplicationMapper : BaseMapper<TownApplicationVO> {

	@Select("SELECT a.id, a.town_id, a.applicant_id,a.message,a.application_time,p.uuid,p.`name`,p.display_name FROM town_application AS a, player AS p WHERE a.applicant_id = p.uuid AND a.town_id = #{townID}")
	fun listOfTownId(townID: Int): List<Map<String, Any>>


	companion object {
		fun TownApplicationMapper.listOfTownIdAsVO(townID: Int): List<TownApplication> {
			return listOfTownId(townID).map {
				val playerInfo = PlayerInfo(
					uuid = UUID.fromString(it["uuid"] as String),
					name = it["name"] as String,
					displayName = it["display_name"] as String
				)
				TownApplication(
					id = it["id"] as Int,
					townID = it["town_id"] as Int,
					applicant = playerInfo,
					message = it["message"] as String,
					applicationTime = (it["application_time"] as LocalDateTime).toDate()
				)
			}
		}
	}

}