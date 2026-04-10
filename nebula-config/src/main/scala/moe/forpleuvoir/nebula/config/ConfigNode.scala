package moe.forpleuvoir.nebula.config

import moe.forpleuvoir.nebula.common.api.{Initializable, Matchable}
import moe.forpleuvoir.nebula.common.util.parents as _parents
import moe.forpleuvoir.nebula.serialization.codec.Serde

import scala.collection.mutable
import scala.util.matching.Regex

trait ConfigNode extends Initializable, Matchable[Regex], Serde {

  def name: String

  def parent: Option[ConfigGroup]

  def root: Option[ConfigManager] = parent.flatMap(_.root)

  private val metadata: mutable.Map[String, Any] = mutable.Map.empty

  def getMetadata(key: String): Option[Any] = metadata.get(key)

  def setMetadata(key: String, value: Any): this.type = {
    metadata.update(key, value)
    this
  }

}

object ConfigNode {

  extension (self: ConfigNode) {
    def parents: List[ConfigNode] = {
      self._parents()(_.parent)
    }
  }

  extension [T <: ConfigNode](self: T) {

    def path: String = self.parent match {
      case Some(parent) => parent.path + "." + self.name
      case None => self.name
    }

    def pathWithOutRoot: String = path.split("\\.").drop(1).mkString(".")

    def isRoot: Boolean = self.parent.isEmpty

    def comment: Option[String] = self.getMetadata("comment").map(_.toString)

    def comment(comment: String): self.type = {
      self.comment = comment
      self
    }

    def comment_=(comment: String): Unit = self.setMetadata("comment", comment)

  }

}