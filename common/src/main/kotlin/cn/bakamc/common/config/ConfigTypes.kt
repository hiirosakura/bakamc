package cn.bakamc.common.config

import java.util.*

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config

 * 文件名 ConfigTypes

 * 创建时间 2022/7/4 0:25

 * @author forpleuvoir

 */
enum class ConfigTypes : ConfigType {
	BOOLEAN,
	INTEGER,
	DOUBLE,
	COLOR,
	STRING,
	STRING_LIST,
	STRING_PAIRS,
	OPTIONS,
	GROUP,
	PAIRS,
	STRING_MAP,
	MAP
	;

	override val type: String
		get() = name.lowercase(Locale.getDefault())
}