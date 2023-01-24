package cn.bakamc.riguru.websocket

import cn.bakamc.common.utils.jsonArray
import cn.bakamc.riguru.config.RiguruConfig
import cn.bakamc.riguru.services.TownServices
import cn.bakamc.riguru.util.broadcast
import cn.bakamc.riguru.util.sendMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.websocket.OnClose
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.websocket

 * 文件名 TownServer

 * 创建时间 2022/9/11 14:39

 * @author forpleuvoir

 */
@ServerEndpoint("/town/{server_id}")
@EnableScheduling
@Component
class TownServer {

	@Autowired
	fun setTownServices(townServices: TownServices) {
		TownServer.townServices = townServices
	}

	@Scheduled(fixedRate = 300000, initialDelay = 60000)
	fun atuSyncData() {
		if (RiguruConfig.needSave) {
			log.info("[Riguru]自动同步小镇信息...")
			syncData()
		}
	}

	companion object {

		private val log = LoggerFactory.getLogger(TownServer::class.java)

		val sessions: MutableList<Session> = ArrayList()

		private lateinit var townServices: TownServices

		fun syncData() {
			val towns = townServices.getAll().values
			sessions.broadcast(jsonArray(towns).toString())
			log.info("小镇信息同步")
		}

		fun syncData(session: Session, serverID: String) {
			val towns = townServices.getAll().values
			session.sendMessage(jsonArray(towns).toString())
			log.info("[{}]小镇信息同步", serverID)
		}
	}

	@OnOpen
	fun onOpen(session: Session, @PathParam("server_id") id: String) {
		sessions.add(session)
		syncData(session, id)
		log.info("[{}]有人订阅了小镇系统服务！", id)
	}

	@OnClose
	fun onClose(session: Session, @PathParam("server_id") id: String) {
		if (sessions.remove(session)) {
			log.info("[{}]有人退订了小镇系统服务!", id)
		}
	}

}