package cn.bakamc.riguru.services

import cn.bakamc.common.player.PlayerInfo
import cn.bakamc.common.town.Town
import cn.bakamc.common.town.TownApplication
import cn.bakamc.common.town.TownMember
import cn.bakamc.riguru.entity.PlayerInfoVO
import cn.bakamc.riguru.entity.TownApplicationVO
import cn.bakamc.riguru.entity.TownApplicationVO.Companion.toVO
import cn.bakamc.riguru.entity.TownMemberVO
import cn.bakamc.riguru.entity.TownMemberVO.Companion.roleVO
import cn.bakamc.riguru.entity.TownRole
import cn.bakamc.riguru.mapper.PlayerInfoMapper
import cn.bakamc.riguru.mapper.TownApplicationMapper
import cn.bakamc.riguru.mapper.TownApplicationMapper.Companion.listOfTownIdAsVO
import cn.bakamc.riguru.mapper.TownMapper
import cn.bakamc.riguru.mapper.TownMemberMapper
import cn.bakamc.riguru.mapper.TownMemberMapper.Companion.getAllByTownIDAsVO
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
	@Autowired val playerInfoMapper: PlayerInfoMapper,
	@Autowired val townApplicationMapper: TownApplicationMapper
) {

	/**
	 * 获取所有小镇
	 * @return Map<Int, Town> 小镇ID:小镇对象
	 */
	fun getAll(): Map<Int, Town> {
		val townVOs = townMapper.selectList(null)
		return buildMap {
			townVOs.forEach { townVOs ->
				val allMembers = townMemberMapper.getAllByTownIDAsVO(townVOs.id!!)
				val members = allMembers.map {
					it.playerInfo
				}
				val major = allMembers.filter { it.roleVO == TownRole.Major }.map {
					it.playerInfo
				}
				val admins = allMembers.filter { it.roleVO == TownRole.Admin }.map {
					it.playerInfo
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
	fun isInTown(player: UUID, town: Town? = null): Pair<Boolean, TownMemberVO?> {
		val memberVO: TownMemberVO? = townMemberMapper.selectOne(QueryWrapper<TownMemberVO>().eq("player_id", player.toString()))
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
	fun join(townID: Int, joiner: UUID): Boolean {
		val isInTown = isInTown(joiner)
		if (isInTown.first) return false
		val memberVO: TownMemberVO? = isInTown.second
		val member = TownMemberVO().apply {
			playerID = joiner.toString()
			townId = townID
			role = TownRole.Member
		}
		val result = if (memberVO == null) townMemberMapper.insert(member)
		else townMemberMapper.update(member, QueryWrapper<TownMemberVO?>().eq("player_id", joiner.toString()))
		return result == 1
	}

	/**
	 * 申请加入小镇 如果原来已经有加入的小镇则会失败
	 * @param town Town
	 * @param applicant PlayerInfo
	 * @return [Boolean] 是否成功
	 */
	fun application(application: TownApplication): Boolean {
		val isInTown = isInTown(application.applicant.uuid)
		if (isInTown.first) return false
		val empty = townApplicationMapper.selectList(
			QueryWrapper<TownApplicationVO?>().eq("town_id", application.townID).eq("applicant_id", application.applicant.uuid())
		).isEmpty()
		if (!empty) return false
		val result = townApplicationMapper.insert(application.toVO(true))
		return result == 1
	}

	/**
	 * 批准加入小镇的申请
	 * @param application TownApplication
	 */
	fun approveApplication(application: TownApplication): Boolean {
		townApplicationMapper.selectById(application.id) ?: return false
		val returnValue = join(application.townID, application.applicant.uuid)
		if (returnValue) townApplicationMapper.deleteById(application.id)
		return returnValue
	}

	/**
	 * 获取对应小镇的申请列表
	 * @param townID [Int]
	 * @return [List]<[TownApplication]>
	 */
	fun applicationList(townID: Int): List<TownApplication> {
		return townApplicationMapper.listOfTownIdAsVO(townID)
	}

	/**
	 * 获取对应小镇的所有成员
	 * @param townID [Int]
	 * @return [List]<[PlayerInfo]>
	 */
	fun members(townID: Int): List<TownMember> {
		return townMemberMapper.getAllByTownIDAsVO(townID)
	}
}