package moe.forpleuvoir.nebula.config.component

import moe.forpleuvoir.nebula.config.ConfigManager

trait ConfigManagerComponent {

  def manager: ConfigManager

  def beginInit(): Unit = {}

  def finishInit(): Unit = {}

  def onSave(): Unit = {}

  def onForcedSave(): Unit = {}

  def onLoad(): Unit = {}
}
