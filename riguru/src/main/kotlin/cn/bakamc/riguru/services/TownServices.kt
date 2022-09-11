package cn.bakamc.riguru.services

import cn.bakamc.common.common.PlayerInfo
import cn.bakamc.common.town.Town
import cn.bakamc.riguru.entity.PlayerInfoVO
import cn.bakamc.riguru.entity.TownMemberVO
import cn.bakamc.riguru.entity.TownRole
import cn.bakamc.riguru.mapper.PlayerInfoMapper
import cn.bakamc.riguru.mapper.TownMapper
import cn.bakamc.riguru.mapper.TownMemberMapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.services

 * 文件名 TownServices

 * 创建时间 2022/9/11 16:54

 * @author forpleuvoir

 */
@Service
class TownServices(
	@Autowired val townMapper: TownMapper,
	@Autowired val townMemberMapper: TownMemberMapper,
	@Autowired val playerInfoMapper: PlayerInfoMapper
) {

	fun getAll(): Map<Int, Town> {
		val townVOs = townMapper.selectList(null)
		return buildMap {
			townVOs.forEach { townVOs ->
				val allMembers = townMemberMapper.getAllByTownID(townVOs.id!!)
				val members = allMembers.filter { it["role_id"] == TownRole.Member.code }.map {
					PlayerInfo(UUID.fromString(it["uuid"] as String), it["name"] as String, it["display_name"] as String)
				}
				val major = allMembers.filter { it["role_id"] == TownRole.Major.code }.map {
					PlayerInfo(UUID.fromString(it["uuid"] as String), it["name"] as String, it["display_name"] as String)
				}.first()
				val admins = allMembers.filter { it["role_id"] == TownRole.Admin.code }.map {
					PlayerInfo(UUID.fromString(it["uuid"] as String), it["name"] as String, it["display_name"] as String)
				}
				this[townVOs.id!!] = Town(
					id = townVOs.id!!,
					name = townVOs.name!!,
					shortName = townVOs.shortName!!,
					mayor = major,
					createTime = townVOs.createTime!!,
					administrators = ConcurrentLinkedDeque(admins),
					members = ConcurrentLinkedDeque(members)
				)
			}
		}
	}

	/**
	 * 加入小镇
	 *
	 * 一个玩家只能加入一个小镇，如果原来已经有加入的小镇则会失败
	 *
	 * @param town [Town] 需要加入的小镇
	 * @param joiner [PlayerInfo] 加入者的信息
	 * @return [Boolean] 是否成功
	 */
	fun join(town: Town, joiner: PlayerInfo): Boolean {
		val playerInfo = playerInfoMapper.selectOne(QueryWrapper<PlayerInfoVO?>().eq("uuid", joiner.uuid.toString()))
		val memberVO: TownMemberVO? = townMemberMapper.selectOne(QueryWrapper<TownMemberVO>().eq("player_id", playerInfo.id))
		if (memberVO != null && memberVO.townId != 0) return false
		val member = TownMemberVO().apply {
			playerID = playerInfo.id
			townId = town.id
			role = TownRole.Member
		}
		if (memberVO == null) townMemberMapper.insert(member)
		else townMemberMapper.update(member, QueryWrapper<TownMemberVO?>().eq("player_id", playerInfo.id))
		return true
	}

}