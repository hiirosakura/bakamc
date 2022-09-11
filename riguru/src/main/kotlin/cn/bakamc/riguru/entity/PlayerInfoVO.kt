package cn.bakamc.riguru.entity

import cn.bakamc.common.common.PlayerInfo
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.*

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.entity

 * 文件名 PlayerInfoVO

 * 创建时间 2022/9/12 0:44

 * @author forpleuvoir

 */
@TableName("player")
class PlayerInfoVO {
	/**
	 * 主键 只是作为索引
	 */
	@TableId(type = IdType.AUTO, value = "id")
	var id: Int? = null

	/**
	 * 玩家uuid
	 */
	@TableField("uuid")
	var uuid: String? = null

	/**
	 * 玩家名
	 */
	@TableField("name")
	var name: String? = null

	/**
	 * 玩家显示名称
	 */
	@TableField("display_name")
	var displayName: String? = null


	val playerInfo: PlayerInfo get() = PlayerInfo(UUID.fromString(uuid!!), name!!, displayName!!)

	companion object {
		fun PlayerInfo.toVO(): PlayerInfoVO {
			return PlayerInfoVO().apply {
				this.uuid = this@toVO.uuid.toString()
				this.name = this@toVO.name
				this.displayName = this@toVO.displayName
			}
		}
	}
}
