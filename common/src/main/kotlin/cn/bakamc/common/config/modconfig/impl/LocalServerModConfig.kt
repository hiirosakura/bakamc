package cn.bakamc.common.config.modconfig.impl

import cn.bakamc.common.api.impl.ServerSavable
import cn.bakamc.common.config.modconfig.ConfigCategory
import kotlin.reflect.full.isSubclassOf

/**
 * 服务器MOD配置

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.modconfig.impl

 * 文件名 LocalServerModConfig

 * 创建时间 2022/8/12 1:46

 * @author forpleuvoir

 */
abstract class LocalServerModConfig<S>(override val modId: String) : LocalModConfig(modId), ServerSavable<S> {

	override fun init(server: S) {
		this::class.nestedClasses.forEach {
			if (it.objectInstance != null && it.isSubclassOf(ConfigCategory::class)) {
				addCategory(it.objectInstance as ConfigCategory)
			}
		}
		this.server = server
		allCategory.forEach { configCategory ->
			configCategory.allConfigs.forEach {
				it.resetDefValue()
			}
		}
		init()
	}

}