package cn.bakamc.common.config.options

import cn.bakamc.common.api.Option
import cn.bakamc.common.api.Switchable
import cn.bakamc.common.config.ConfigValue

/**
 * 选项配置

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options

 * 文件名 IConfigOption

 * 创建时间 2022/7/4 20:38

 * @author forpleuvoir

 */
interface IConfigOption : ConfigValue<Option>, Switchable {

	fun getOptions(): LinkedHashSet<Option>

}