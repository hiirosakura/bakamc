package cn.bakamc.common.town

import com.google.common.collect.ImmutableList
import java.lang.Exception
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 小镇管理器
 *
 * 每一个服务端都应该维护一个此对象，定时与riguru中的此对象同步数据
 *
 * 服务端不应该修改此对象中的数据,应该请求到riguru中来修改数据

 * 项目名 bakamc

 * 包名 cn.bakamc.common.town

 * 文件名 TownManager

 * 创建时间 2022/8/30 17:42

 * @author forpleuvoir

 */
abstract class TownManager {

	protected val towns: MutableMap<Int, Town> = ConcurrentHashMap()

	val townNames: List<String> get() = buildList { towns.values.forEach { add(it.name) } }

	operator fun get(townID: Int): Town? = towns[townID]

	operator fun get(townName: String): Town? = towns.values.findLast { it.name == townName }

	fun getByMayor(mayorID: UUID): Town? = towns.values.findLast { it.mayor == mayorID }

	fun <T> getMayor(townID: Int, converter: (UUID) -> T): T? {
		return this[townID]?.mayor?.run(converter)
	}

	fun <T> getMayor(townName: String, converter: (UUID) -> T): T? {
		return this[townName]?.mayor?.run(converter)
	}

	fun <T> getAdministrators(townID: Int, converter: (UUID) -> T): List<T> {
		val list = ArrayList<T>()
		this[townID]?.let { town ->
			town.administrators.forEach { list.add(converter(it)) }
		}
		return list
	}

	fun <T> getAdministrators(townName: String, converter: (UUID) -> T): List<T> {
		val list = ArrayList<T>()
		this[townName]?.let { town ->
			town.administrators.forEach { list.add(converter(it)) }
		}
		return list
	}

	fun <T> getMembers(townID: Int, converter: (UUID) -> T): List<T> {
		val list = ArrayList<T>()
		this[townID]?.let { town ->
			town.members.forEach { list.add(converter(it)) }
		}
		return list
	}

	fun <T> getMembers(townName: String, converter: (UUID) -> T): List<T> {
		val list = ArrayList<T>()
		this[townName]?.let { town ->
			town.members.forEach { list.add(converter(it)) }
		}
		return list
	}

	fun update(towns: List<Town>) {
		this.towns.clear()
		towns.forEach {
			this.towns[it.id] = it
		}
	}

	fun getAll(): List<Town> = ImmutableList.copyOf(towns.values)

	/**
	 * 应该是个异步方法
	 * @param onSuccess Function0<Unit> 同步成功时
	 * @param onException Function1<Exception, Unit> 同步失败时
	 */
	abstract fun syncData(onSuccess: () -> Unit, onException: (Exception) -> Unit)
}