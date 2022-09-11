package cn.bakamc.riguru.mapper

import cn.bakamc.riguru.entity.TownMemberVO
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

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

	@Select("SELECT player.*,town_member.role_id FROM player,town,town_member WHERE town_member.town_id = #{townID} AND town_member.player_id = player.id")
	fun getAllByTownID(townID: Int): List<Map<String, Any>>

}
