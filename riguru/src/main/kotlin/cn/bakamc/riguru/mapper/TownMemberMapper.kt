package cn.bakamc.riguru.mapper

import cn.bakamc.common.player.PlayerInfo
import cn.bakamc.common.town.TownMember
import cn.bakamc.riguru.entity.TownMemberVO
import cn.bakamc.riguru.entity.TownRole
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import java.util.*

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.mapper

 * 文件名 TownMemberMapper

 * 创建时间 2022/9/11 17:04

 * @author forpleuvoir

 */
@Mapper
interface TownMemberMapper : BaseMapper<TownMemberVO> {

	@Select("SELECT player.*,town_member.role_id FROM player,town,town_member WHERE town_member.town_id = #{townID} AND town_member.player_id = player.uuid")
	fun getAllByTownID(townID: Int): List<Map<String, Any>>

	/**
	 * 默认方法写在伴生对象里，不会被注入
	 */
	companion object {
		fun TownMemberMapper.getAllByTownIDAsVO(townID: Int): List<TownMember> {
			return getAllByTownID(townID).map {
				val playerInfo = PlayerInfo(
					uuid = UUID.fromString(it["uuid"] as String),
					name = it["name"] as String,
					displayName = it["display_name"] as String
				)
				val role = TownRole.ofCode(it["role_id"] as Int)
				TownMember(playerInfo, townID, role.description)
			}
		}

	}

}
