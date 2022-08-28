package cn.bakamc.common.config.modconfig

import cn.bakamc.common.api.Initializable
import cn.bakamc.common.api.Savable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

/**
 * mod配置

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.modconfig

 * 文件名 ModConfig

 * 创建时间 2022/8/11 23:25

 * @author forpleuvoir

 */
interface ModConfig : Savable, Initializable {

	/**
	 * 保存配置到文件
	 */
	override fun save()

	override fun saveAsync() {
		CompletableFuture.runAsync {
			save()
		}
	}

	/**
	 * 加载配置到内存
	 */
	override fun load()

	override fun loadAsync() {
		CompletableFuture.runAsync {
			load()
		}
	}

	/**
	 * 当配置关闭时
	 */
	fun close() = save()

	fun addCategory(category: ConfigCategory)

	val modId: String

	/**
	 * 获取所有配置
	 * @return List<Config<*>>
	 */
	val allCategory: Collection<ConfigCategory>

	/**
	 * 是否有配置发生改变
	 * @return Boolean
	 */
	val isChanged: AtomicBoolean

	override val needSave: Boolean
		get() = isChanged.get()
}