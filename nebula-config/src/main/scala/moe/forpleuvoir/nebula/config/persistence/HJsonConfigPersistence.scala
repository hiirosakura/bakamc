package moe.forpleuvoir.nebula.config.persistence

import moe.forpleuvoir.nebula.config.{ConfigGroup, ConfigManager, ConfigNode}
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.hjson.{HJsonCommentedEncoder, HJsonDialect}

class HJsonConfigPersistence(
  manager: ConfigManager
) extends ConfigPersistence {

  private val encoder = new ConfigEncoder(manager)

  override def wrapFileName(fileName: String): String = s"$fileName.hjson"

  override def dataToString(data: SerializeElement): String = encoder.encode(data)

  override def stringToData(str: String): SerializeElement =
    HJsonDialect.parse(str).get

}

object HJsonConfigPersistence {
  def apply(manager: ConfigManager): HJsonConfigPersistence = new HJsonConfigPersistence(manager)
}


private class ConfigEncoder(manager: ConfigGroup) extends HJsonCommentedEncoder {

  private lazy val comments: Map[String, String] =
    manager.flat.view.filter(_.comment.nonEmpty).map(node => node.path -> node.comment.get).toMap

  override def getComment(path: String): Option[String] = comments.get(path)

}