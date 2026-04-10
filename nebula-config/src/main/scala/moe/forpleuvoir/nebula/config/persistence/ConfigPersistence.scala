package moe.forpleuvoir.nebula.config.persistence

import moe.forpleuvoir.nebula.serialization.base.SerializeElement

trait ConfigPersistence {

  def wrapFileName(fileName: String): String

  def dataToString(data: SerializeElement): String

  def stringToData(str: String): SerializeElement

}
