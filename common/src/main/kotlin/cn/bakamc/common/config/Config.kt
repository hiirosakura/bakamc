package cn.bakamc.common.config

import cn.bakamc.common.api.*
import cn.bakamc.common.api.serialization.JsonSerializer

/**
 * 单个配置

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config

 * 文件名 Config

 * 创建时间 2022/7/3 23:55

 * @author forpleuvoir

 */
interface Config<T> : Option, ConfigValue<T>, Notifiable<Any>, Resettable, Matchable, JsonSerializer, Initializable {

	val type: ConfigType

}