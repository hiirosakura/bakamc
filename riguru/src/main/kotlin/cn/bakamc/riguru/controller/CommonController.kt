package cn.bakamc.riguru.controller

import cn.bakamc.riguru.config.RiguruConfig
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.riguru.controller

 * 文件名 CommonController

 * 创建时间 2022/11/16 23:17

 * @author forpleuvoir

 */
@Controller
@RequestMapping("/common_config")
class CommonController {

	@GetMapping
	@ResponseBody
	fun get(): String {
		return RiguruConfig.serialization.toString()
	}
}