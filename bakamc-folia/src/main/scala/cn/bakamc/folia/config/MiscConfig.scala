package cn.bakamc.folia.config

import moe.forpleuvoir.nebula.config.item.ConfigBoolean
import moe.forpleuvoir.nebula.config.{Comment, ConfigGroup}

object MiscConfig extends ConfigGroup("misc") {

  @Comment("是否启用玩家加入消息")
  val enablePlayerJoinMessage = ConfigBoolean("enablePlayerJoinMessage", true)


}
