package cn.bakamc.riguru.services

import cn.bakamc.common.common.PlayerInfo
import cn.bakamc.common.town.Town
import cn.bakamc.common.town.TownApplication
import cn.bakamc.riguru.entity.PlayerInfoVO
import cn.bakamc.riguru.entity.TownApplicationVO.Companion.toVO
import cn.bakamc.riguru.entity.TownMemberVO
import cn.bakamc.riguru.entity.TownRole
import cn.bakamc.riguru.mapper.PlayerInfoMapper
import cn.bakamc.riguru.mapper.TownApplicationMapper
import cn.bakamc.riguru.mapper.TownMapper
import cn.bakamc.riguru.mapper.TownMemberMapper
import cn.bakamc.riguru.mapper.TownMemberMapper.Companion.getAllByTownIDAsVO
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
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
	@Autowired val playerInfoMapper: PlayerInfoMapper,
	@Autowired val townApplicationMapper: TownApplicationMapper
) {

	fun getAll(): Map<Int, Town> {
		val townVOs = townMapper.selectList(null)
		return buildMap {
			townVOs.forEach { townVOs ->
				val allMembers = townMemberMapper.getAllByTownIDAsVO(townVOs.id!!)
				val members = allMembers.map {
					it.first.playerInfo
				}
				val major = allMembers.filter { it.second == TownRole.Major }.map {
					it.first.playerInfo
				}
				val admins = allMembers.filter { it.second == TownRole.Admin }.map {
					it.first.playerInfo
				}
				this[townVOs.id!!] = Town(
					id = townVOs.id!!,
					name = townVOs.name!!,
					shortName = townVOs.shortName!!,
					mayor = ConcurrentLinkedDeque(major),
					createTime = townVOs.createTime!!,
					admin = ConcurrentLinkedDeque(admins),
					member = ConcurrentLinkedDeque(members)
				)
			}
		}
	}

	/**
	 * 是否已经加入某个小镇
	 * @param player [PlayerInfo]
	 * @param town [Town] 如果为空则判断是否加入了任何小镇
	 * @return [Boolean] [PlayerInfoVO] [TownMemberVO]
	 */
	fun isInTown(player: PlayerInfo, town: Town? = null): Pair<Boolean, TownMemberVO?> {
		val memberVO: TownMemberVO? = townMemberMapper.selectOne(QueryWrapper<TownMemberVO>().eq("player_id", player.uuid()))
		return if (town != null) (memberVO != null && memberVO.townId == town.id) to memberVO
		else (memberVO != null && memberVO.townId != 0) to memberVO
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
		val isInTown = isInTown(joiner)
		if (isInTown.first) return false
		val memberVO: TownMemberVO? = isInTown.second
		val member = TownMemberVO().apply {
			playerID = joiner.uuid()
			townId = town.id
			role = TownRole.Member
		}
		val result = if (memberVO == null) townMemberMapper.insert(member)
		else townMemberMapper.update(member, QueryWrapper<TownMemberVO?>().eq("player_id", joiner.uuid()))
		return result == 1
	}

	/**
	 * 申请加入小镇 如果原来已经有加入的小镇则会失败
	 * @param town Town
	 * @param applicant PlayerInfo
	 * @return [Boolean] 是否成功
	 */
	fun application(application: TownApplication): Boolean {
		val isInTown = isInTown(application.applicant)
		if (isInTown.first) return false
		val result = townApplicationMapper.insert(application.toVO())
		return result == 1
	}

	/**
	 * 批准加入小镇的申请
	 * @param application TownApplication
	 */
	fun approveApplication(application: TownApplication):Boolean{
		val isInTown = isInTown(application.applicant)
		if (isInTown.first) return false

	}
}