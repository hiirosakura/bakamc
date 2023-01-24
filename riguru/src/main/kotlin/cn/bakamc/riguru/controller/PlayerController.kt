package cn.bakamc.riguru.controller

import cn.bakamc.common.player.PlayerInfo
import cn.bakamc.common.utils.parseToJsonObject
import cn.bakamc.riguru.websocket.PlayerServer
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/player/{server_id}")
class PlayerController {

	@PostMapping("/join")
	@ResponseBody
	fun playerJoin(@RequestBody playerInfo: String, @PathVariable("server_id") serverID: String) {
		val player = PlayerInfo.deserialize(playerInfo.parseToJsonObject)
		PlayerServer.onPlayerJoin(player, serverID)
	}

	@PostMapping("/left")
	@ResponseBody
	fun playerLeft(@RequestBody playerInfo: String, @PathVariable("server_id") serverID: String) {
		val player = PlayerInfo.deserialize(playerInfo.parseToJsonObject)
		PlayerServer.onPlayerLeft(player, serverID)
	}

	@GetMapping("/sync")
	@ResponseBody
	fun sync() {
		PlayerServer.syncData()
	}

}