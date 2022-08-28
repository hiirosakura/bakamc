package cn.bakamc.common.config.options

import cn.bakamc.common.config.Config
import cn.bakamc.common.config.ConfigValue

/**
 * 配置组

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options

 * 文件名 IConfigGroup

 * 创建时间 2022/7/4 21:29

 * @author forpleuvoir

 */
interface IConfigGroup : ConfigValue<Set<Config<*>>> {

	fun getConfigFromKey(key: String): Config<*>?

	fun getKeys(): Set<String>

	fun containsKey(key: String): Boolean = getKeys().contains(key)

}