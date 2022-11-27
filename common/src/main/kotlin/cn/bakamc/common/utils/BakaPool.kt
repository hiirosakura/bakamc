package cn.bakamc.common.utils

import org.java_websocket.util.NamedThreadFactory
import java.util.concurrent.Executors

/**
 *

 * 项目名 bakamc

 * 包名 cn.bakamc.common.utils

 * 文件名 BakaPool

 * 创建时间 2022/11/26 11:29

 * @author forpleuvoir

 */
object BakaPool {

	private val factory = NamedThreadFactory("BakaMC")

	private val pool = Executors.newCachedThreadPool(factory)

	@JvmStatic
	fun execute(command: Runnable) {
		pool.execute(command)
	}

	fun execute(command: () -> Unit) {
		pool.execute(command)
	}

}