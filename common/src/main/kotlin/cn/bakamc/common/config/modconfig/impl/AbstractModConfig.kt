package cn.bakamc.common.config.modconfig.impl

import cn.bakamc.common.config.modconfig.ConfigCategory
import cn.bakamc.common.config.modconfig.ModConfig
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.full.isSubclassOf

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.modconfig.impl

 * 文件名 AbstractModConfig

 * 创建时间 2022/8/12 13:58

 * @author forpleuvoir

 */
abstract class AbstractModConfig:ModConfig {

	private val categories = ConcurrentLinkedQueue<ConfigCategory>()

	override val allCategory: Collection<ConfigCategory> = categories

	override fun addCategory(category: ConfigCategory) {
		categories.add(category)
	}

	override fun init() {
		this::class.nestedClasses.forEach {
			if (it.objectInstance != null && it.isSubclassOf(ConfigCategory::class)) {
				addCategory(it.objectInstance as ConfigCategory)
			}
		}
		allCategory.forEach {
			it.init()
			it.allConfigs.forEach { c ->
				c.subscribeChange(this) {
					isChanged.set(true)
				}
			}
		}
		load()
	}

	override val isChanged: AtomicBoolean = AtomicBoolean(false)
}