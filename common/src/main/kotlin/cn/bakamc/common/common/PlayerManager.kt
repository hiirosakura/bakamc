package cn.bakamc.common.common

import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * 玩家管理器 应该是保存当前服务器所有在线的玩家信息

 * 项目名 bakamc

 * 包名 cn.bakamc.common.common

 * 文件名 PlayerManager

 * 创建时间 2022/9/6 23:12

 * @author forpleuvoir

 */
class PlayerManager {

	private val players: Deque<PlayerCurrentInfo> = ConcurrentLinkedDeque()

}