package cn.bakamc.riguru.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.*

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.entity

 * 文件名 TownMemberVO

 * 创建时间 2022/9/11 17:01

 * @author forpleuvoir

 */
@TableName("town_member")
class TownMemberVO {

	@TableId("player_id")
	var playerID: String? = null

	@TableField("town_id")
	var townId: Int? = null

	@TableField("role_id")
	var role: TownRole? = null

}