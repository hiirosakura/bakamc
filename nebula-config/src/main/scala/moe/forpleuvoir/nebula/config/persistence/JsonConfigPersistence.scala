package moe.forpleuvoir.nebula.config.persistence

import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.json.JsonDialect

object JsonConfigPersistence extends ConfigPersistence {

  override def wrapFileName(fileName: String): String = s"$fileName.json"

  override def dataToString(data: SerializeElement): String =
    JsonDialect.encode(data)

  override def stringToData(str: String): SerializeElement =
    JsonDialect.parse(str).get

}
