package cn.bakamc.riguru.services

import cn.bakamc.common.common.PlayerInfo
import cn.bakamc.riguru.entity.PlayerInfoVO
import cn.bakamc.riguru.entity.PlayerInfoVO.Companion.toVO
import cn.bakamc.riguru.mapper.PlayerInfoMapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.services

 * 文件名 PlayerInfoServices

 * 创建时间 2022/9/12 13:12

 * @author forpleuvoir

 */
@Service
class PlayerInfoServices(
	@Autowired val playerInfoMapper: PlayerInfoMapper
) {

	/**
	 * 所有玩家加入游戏时都应该执行该方法
	 * @param playerInfo PlayerInfo
	 */
	fun saveOrUpdate(playerInfo: PlayerInfo) {
		val vo = playerInfoMapper.selectOne(QueryWrapper<PlayerInfoVO?>().eq("uuid", playerInfo.uuid()))
		if (vo != null) playerInfoMapper.updateById(playerInfo.toVO())
		else playerInfoMapper.insert(playerInfo.toVO())
	}

}