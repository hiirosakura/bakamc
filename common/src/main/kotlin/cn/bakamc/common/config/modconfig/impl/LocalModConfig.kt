package cn.bakamc.common.config.modconfig.impl

import com.google.gson.JsonObject
import cn.bakamc.common.config.ConfigUtil
import java.nio.file.Path

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.modconfig.impl

 * 文件名 LocalModConfig

 * 创建时间 2022/8/12 0:46

 * @author forpleuvoir

 */
abstract class LocalModConfig(override val modId: String) : AbstractModConfig() {
	abstract fun localConfigPath(): Path

	override fun save() {
		ConfigUtil.run {
			val configFile = configFile(modId, localConfigPath())
			val json = JsonObject()
			allCategory.forEach {
				writeConfigCategory(json, it)
			}
			writeJsonToFile(json, configFile)
			isChanged.set(false)
		}
	}

	override fun load() {
		ConfigUtil.run {
			val configFile = configFile(modId, localConfigPath())
			paresJsonFile(configFile) { json ->
				json as JsonObject
				allCategory.forEach {
					readConfigCategory(json, it)
				}
			}
		}
	}

}