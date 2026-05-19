package cn.bakamc.folia.config

import cn.bakamc.folia.BakaMC
import moe.forpleuvoir.nebula.config.{ConfigNode, DeserializationException, ExceptionHandler, SerializationException}

import language.implicitConversions

object PluginExceptionHandler extends ExceptionHandler {

  override def onSerializationException(config: ConfigNode, e: SerializationException): Unit = {
    BakaMC.logger.error(s"配置[${config.name}]序列化异常", e)
  }

  override def onDeserializationException(config: ConfigNode, e: DeserializationException): Unit = {
    BakaMC.logger.error(s"配置[${config.name}]反序列化异常", e)
  }
}
