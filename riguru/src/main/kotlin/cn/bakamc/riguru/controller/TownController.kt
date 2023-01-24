package cn.bakamc.riguru.controller

import cn.bakamc.common.town.TownApplication
import cn.bakamc.common.utils.jsonArray
import cn.bakamc.common.utils.parseToJsonObject
import cn.bakamc.riguru.services.TownServices
import cn.bakamc.riguru.websocket.TownServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.controller

 * 文件名 TownController

 * 创建时间 2022/11/23 16:04

 * @author forpleuvoir

 */
@Controller
@RequestMapping("/town/{server_ID}")
class TownController {

	@Autowired
	private lateinit var townServices: TownServices

	@GetMapping
	@ResponseBody
	fun getData(){
		syncData()
	}

	@PostMapping("/application")
	@ResponseBody
	fun application(@RequestBody application: String): Boolean {
		return townServices.application(TownApplication.deserialize(application.parseToJsonObject))
	}

	@GetMapping("/application")
	@ResponseBody
	fun getApplications(@RequestParam("town_id") townID: Int): String {
		return jsonArray(townServices.applicationList(townID)).toString()
	}

	@PostMapping("/approve_application")
	@ResponseBody
	fun approveApplication(@RequestParam("town_id") townID: Int, @RequestParam("applicant_uuid") applicantUUID: String): Boolean {
		val success = townServices.join(townID, UUID.fromString(applicantUUID))
		if(success) syncData()
		return success
	}

	@GetMapping("/sync")
	@ResponseBody
	fun syncData() {
		TownServer.syncData()
	}


}