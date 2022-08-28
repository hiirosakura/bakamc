package cn.bakamc.common.config.options

import cn.bakamc.common.api.Toggleable
import cn.bakamc.common.config.ConfigValue

/**
 * 布尔配置

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options

 * 文件名 IConfigBoolean

 * 创建时间 2022/7/4 18:26

 * @author forpleuvoir

 */
interface IConfigBoolean : ConfigValue<Boolean>, Toggleable {
	override fun toggle() {
		setValue(!getValue())
	}
}