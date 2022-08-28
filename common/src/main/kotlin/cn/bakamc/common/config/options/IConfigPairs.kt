package cn.bakamc.common.config.options

import cn.bakamc.common.config.ConfigValue

/**
 * 键值对集合配置

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.config.options

 * 文件名 IConfigPairs

 * 创建时间 2022/7/4 21:31

 * @author forpleuvoir

 */
interface IConfigPairs<K, V> : ConfigValue<List<Pair<K, V>>> {

	fun add(key: K, value: V): Boolean

	fun remove(key: K, value: V): Boolean

	fun remove(index: Int): Pair<K, V>?

	fun get(key: K): List<V>

	fun get(index: Int): Pair<K, V>?

	fun clear()

}