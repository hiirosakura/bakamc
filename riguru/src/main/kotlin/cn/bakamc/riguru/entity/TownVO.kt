package cn.bakamc.riguru.entity

import cn.bakamc.common.utils.exception.DataFormatException
import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import java.util.*

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.entity

 * 文件名 Town

 * 创建时间 2022/9/11 16:40

 * @author forpleuvoir

 */
@TableName("town")
class TownVO {
	@TableId(type = IdType.AUTO, value = "id")
	var id: Int? = 0

	@TableField("town_name")
	var name: String? = null

	@TableField("short_name")
	var shortName: String? = null

	@TableField("create_time")
	var createTime: Date? = null

	fun asNewData(): TownVO {
		this.id = null
		if (this.name == null) throw DataFormatException("小镇名不能为空")
		if (this.shortName == null) throw DataFormatException("小镇短名不能为空")
		if (this.createTime == null) throw DataFormatException("小镇创建时间不能为空")
		return this
	}
}