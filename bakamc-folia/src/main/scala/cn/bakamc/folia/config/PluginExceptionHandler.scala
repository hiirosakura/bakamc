package cn.bakamc.folia.config

import moe.forpleuvoir.nebula.config.{ConfigNode, DeserializationException, ExceptionHandler, SerializationException}
import org.slf4j.Logger

import scala.compiletime.uninitialized

object PluginExceptionHandler extends ExceptionHandler {

  var logger: Logger = uninitialized

  override def onSerializationException(config: ConfigNode, e: SerializationException): Unit = {
    logger.error(s"配置[${config.name}]序列化异常", e)
  }

  override def onDeserializationException(config: ConfigNode, e: DeserializationException): Unit = {
    logger.error(s"配置[${config.name}]反序列化异常", e)
  }
}
