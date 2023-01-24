package cn.bakamc.common.town

import cn.bakamc.common.api.WebSocketHandler
import cn.bakamc.common.common.SimpleWebSocketClient
import cn.bakamc.common.config.common.ServerConfig
import cn.bakamc.common.player.PlayerInfo
import cn.bakamc.common.utils.isIn
import cn.bakamc.common.utils.net.httpGet
import cn.bakamc.common.utils.net.httpPost
import cn.bakamc.common.utils.parseToJsonArray
import cn.bakamc.common.utils.toJsonStr
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

 * 文件名 TownHandler

 * 创建时间 2022/8/30 17:42

 * @author forpleuvoir

 */
abstract class TownHandler(val config: ServerConfig) : WebSocketHandler {

	private val towns: MutableMap<Int, Town> = ConcurrentHashMap()

	private val townList: Collection<Town> get() = towns.values

	val townNames: List<String> get() = buildList { towns.values.forEach { add(it.name) } }

	operator fun get(townID: Int): Town? = towns[townID]

	operator fun get(townName: String): Town? = towns.values.findLast { it.name == townName }

	private fun url(path: String): String {
		return "${config.riguruHttpAddress}/town/${config.serverId}/${path}"
	}

	open fun update(towns: List<Town>) {
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

	override val webSocketClient =
		SimpleWebSocketClient("town", "${config.riguruWebSocketAddress}/town/${config.serverInfo.serverID}")
			.onMessage(::onMessage)
			.salt(config.riguruSecret)


	protected open fun onMessage(message: String) {
		try {
			update(message.parseToJsonArray.map { Town.deserialize(it) })
		} catch (e: Exception) {
			println("小镇信息解析失败:${message}")
		}
	}

	fun syncData() {
		httpGet(url("sync")).sendAsync {}
	}

	fun createTown(town: Town, callback: (Boolean) -> Unit) {
		httpPost(url("create_town"), town.toJsonStr())
			.sendAsyncGetBody {
				callback(it.toBoolean())
			}
	}

	fun application(application: TownApplication, callback: (Boolean) -> Unit) {
		httpPost(url("application"), application.toJsonStr())
			.sendAsyncGetBody {
				callback(it.toBoolean())
			}
	}

	fun applicationList(town: Town, callback: (Iterable<TownApplication>) -> Unit) {
		httpGet(url("application"))
			.params("town_id" to town.id)
			.sendAsyncGetBody { body ->
				callback(body.parseToJsonArray.map { TownApplication.deserialize(it) })
			}
	}

	fun approveApplication(town: Town, approver: PlayerInfo, isOP: Boolean, applicantUUID: UUID, callback: (Boolean) -> Unit) {
		if (approver.isIn(town.admin) || isOP) {
			httpPost(url("approve_application"))
				.params(
					"town_id" to town.id,
					"applicant_uuid" to applicantUUID.toString()
				)
				.sendAsyncGetBody {
					callback(it.toBoolean())
				}
		} else {
			callback(false)
		}
	}

}