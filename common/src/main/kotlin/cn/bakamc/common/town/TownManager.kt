package cn.bakamc.common.town

import cn.bakamc.common.api.WSMessageType.Town.TOWN_SYNC_ALL_DATA
import cn.bakamc.common.api.parseToWSMessage
import cn.bakamc.common.common.SimpleWebSocketClient
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.utils.parseToJsonArray
import com.google.common.collect.ImmutableList
import java.util.*
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
abstract class TownManager(val config: ServerConfig) {

	protected val towns: MutableMap<Int, Town> = ConcurrentHashMap()

	protected val townList: Collection<Town> get() = towns.values

	val townNames: List<String> get() = buildList { towns.values.forEach { add(it.name) } }

	operator fun get(townID: Int): Town? = towns[townID]

	operator fun get(townName: String): Town? = towns.values.findLast { it.name == townName }

	fun update(towns: List<Town>) {
		this.towns.clear()
		towns.forEach {
			this.towns[it.id] = it
		}
	}

	fun getAll(): List<Town> = ImmutableList.copyOf(towns.values)

	fun getByPlayerID(uuid: UUID): Town {
		return townList.find {
			it.member.find { member -> member.uuid == uuid } != null
		} ?: Town.NONE
	}

	protected val webSocketClient = SimpleWebSocketClient("${config.riguruWebSocketAddress}/town", ::onMessage)

	fun connect() = webSocketClient.connect()

	fun reconnect() = webSocketClient.reconnect()

	fun close() = webSocketClient.close()

	protected fun onMessage(message: String) {
		message.parseToWSMessage {
			when (type) {
				TOWN_SYNC_ALL_DATA -> update(data.parseToJsonArray.map { Town.deserialize(it) })
			}
		}
	}

}