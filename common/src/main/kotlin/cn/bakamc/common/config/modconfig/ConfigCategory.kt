package cn.bakamc.common.config.modconfig

import cn.bakamc.common.api.Initializable
import cn.bakamc.common.config.Config

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.modconfig

 * 文件名 ConfigCategory

 * 创建时间 2022/8/12 0:29

 * @author forpleuvoir

 */
interface ConfigCategory : Initializable {

	val name: String

	val allConfigs: Collection<Config<*>>

}