package cn.bakamc.common.api.impl

import cn.bakamc.common.api.Initializable
import cn.bakamc.common.api.Savable
import java.util.concurrent.ConcurrentHashMap

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.api.impl

 * 文件名 SavableHandler

 * 创建时间 2022/8/23 0:36

 * @author forpleuvoir

 */
abstract class SavableHandler<T : Savable> : Savable, Initializable {

	protected val configs = ConcurrentHashMap<String, T>()

	override fun load() {
		configs.values.forEach { it.load() }
	}

	override fun loadAsync() {
		configs.values.forEach { it.loadAsync() }
	}

	override fun save() {
		configs.values.forEach { it.save() }
	}

	override fun saveAsync() {
		configs.values.forEach { it.saveAsync() }
	}

}