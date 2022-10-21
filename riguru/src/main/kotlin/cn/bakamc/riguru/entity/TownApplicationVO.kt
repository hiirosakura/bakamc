package cn.bakamc.riguru.entity

import cn.bakamc.common.town.TownApplication
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.*

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.entity

 * 文件名 TownApplicationVO

 * 创建时间 2022/9/12 11:16

 * @author forpleuvoir

 */
@TableName("town_application")
class TownApplicationVO {

	/**
	 * 主键 只是作为索引
	 */
	@TableId(type = IdType.AUTO, value = "id")
	var id: Int? = null

	@TableField("town_id")
	var townID: Int? = null

	@TableField("applicant_id")
	var applicantID: String? = null

	@TableField("message")
	var message: String? = null

	@TableField("application_time")
	var applicationTime: Date? = null

	companion object {
		fun TownApplication.toVO(asNewData: Boolean = false): TownApplicationVO = TownApplicationVO().apply {
			this@apply.id = if (asNewData) null else this@toVO.id
			this@apply.townID = this@toVO.townID
			this@apply.applicantID = this@toVO.applicant.uuid()
			this@apply.message = this@toVO.message
			this@apply.applicationTime = this@toVO.applicationTime
		}
	}

}