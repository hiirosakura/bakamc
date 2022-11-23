package cn.bakamc.riguru.controller

import cn.bakamc.common.api.WSMessage
import cn.bakamc.common.api.WSMessageType.Town
import cn.bakamc.common.town.TownApplication
import cn.bakamc.common.utils.jsonArray
import cn.bakamc.common.utils.parseToJsonObject
import cn.bakamc.riguru.services.TownServices
import cn.bakamc.riguru.util.broadcast
import cn.bakamc.riguru.websocket.TownServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.controller

 * 文件名 TownController

 * 创建时间 2022/11/23 16:04

 * @author forpleuvoir

 */
@Controller
@RequestMapping("town")
class TownController {

	@Autowired
	private lateinit var townServices: TownServices

	@PostMapping("application")
	@ResponseBody
	fun application(@RequestBody application:String):Boolean{
		println(TownApplication.deserialize(application.parseToJsonObject))
		return true
//		return townServices.application(application)
	}

	fun syncData() {
		val towns = townServices.getAll().values
		TownServer.sessions.broadcast(WSMessage(Town.TOWN_SYNC_ALL_DATA, jsonArray(towns).toString()))
	}


}