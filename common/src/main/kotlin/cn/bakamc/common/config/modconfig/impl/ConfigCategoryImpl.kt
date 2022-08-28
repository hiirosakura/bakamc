package cn.bakamc.common.config.modconfig.impl

import cn.bakamc.common.api.Option
import cn.bakamc.common.config.Config
import cn.bakamc.common.config.modconfig.ConfigCategory
import cn.bakamc.common.config.modconfig.ModConfig
import cn.bakamc.common.config.options.impl.*
import cn.bakamc.common.utils.color.Color
import java.util.concurrent.ConcurrentLinkedQueue

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.modconfig.impl

 * 文件名 AbstractConfigCategory

 * 创建时间 2022/8/12 0:36

 * @author forpleuvoir

 */
@Suppress("SameParameterValue")
open class ConfigCategoryImpl(final override val name: String, modConfig: ModConfig) : ConfigCategory {

	private val modId = modConfig.modId

	private val configs = ConcurrentLinkedQueue<Config<*>>()

	override val allConfigs: Collection<Config<*>> = configs

	override fun init() {
		configs.forEach { it.init() }
	}

	fun <C : Config<*>> addConfig(config: C): C {
		config.apply {
			configs.add(this)
			return this
		}
	}

	fun displayName(key: String): String {
		return "$modId.config.$name.$key"
	}

	fun description(key: String): String {
		return "$modId.config.$name.description.$key"
	}

	protected fun configBoolean(key: String, defaultValue: Boolean): ConfigBoolean =
		addConfig(ConfigBoolean(key, displayName(key), description(key), defaultValue))

	protected fun configColor(key: String, defaultValue: Color): ConfigColor =
		addConfig(ConfigColor(key, displayName(key), description(key), defaultValue))

	protected fun configDouble(
		key: String,
		defaultValue: Double,
		minValue: Double = Double.MIN_VALUE,
		maxValue: Double = Double.MAX_VALUE,
	): ConfigDouble =
		addConfig(ConfigDouble(key, displayName(key), description(key), defaultValue, minValue, maxValue))

	protected fun configGroup(key: String, defaultValue: Set<Config<*>>): ConfigGroup =
		addConfig(ConfigGroup(key, displayName(key), description(key), defaultValue))

	protected fun configInteger(
		key: String,
		defaultValue: Int,
		minValue: Int = Int.MIN_VALUE,
		maxValue: Int = Int.MAX_VALUE,
	): ConfigInteger =
		addConfig(ConfigInteger(key, displayName(key), description(key), defaultValue, minValue, maxValue))

	protected fun configOption(key: String, options: LinkedHashSet<Option>, defaultValue: Option = options.first()): ConfigOption =
		addConfig(ConfigOption(key, displayName(key), description(key), options, defaultValue))

	protected fun configString(key: String, defaultValue: String): ConfigString =
		addConfig(ConfigString(key, displayName(key), description(key), defaultValue))

	protected fun configStringList(key: String, defaultValue: List<String>): ConfigStringList =
		addConfig(ConfigStringList(key, displayName(key), description(key), defaultValue))

	protected fun configStringMap(key: String, defaultValue: Map<String, String>): ConfigStringMap =
		addConfig(ConfigStringMap(key, displayName(key), description(key), defaultValue))

	protected fun configStringPairs(key: String, defaultValue: List<Pair<String, String>>): ConfigStringPairs =
		addConfig(ConfigStringPairs(key, displayName(key), description(key), defaultValue))

}


