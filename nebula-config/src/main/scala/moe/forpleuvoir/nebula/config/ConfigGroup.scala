package moe.forpleuvoir.nebula.config

import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializeObject}
import moe.forpleuvoir.nebula.serialization.extension.SerObjectOps.:=

import java.lang.reflect.Field
import scala.annotation.tailrec
import scala.util.matching.Regex

trait ConfigGroup(
  override val name: String
) extends ConfigNode {

  given ConfigGroup = this

  private var _parent: Option[ConfigGroup] = None

  private[config] def parent_=(parent: Option[ConfigGroup]): Unit = _parent = parent

  override def parent: Option[ConfigGroup] = _parent

  private var _children: List[ConfigNode] = Nil

  private var _initialized: Boolean = false

  override def initialization(): Unit = {
    require(!_initialized, "ConfigGroup has been initialized")
    this.discoverAndRegisterNodes()
    _children.foreach(_.initialization())
    _initialized = true
  }

  def children: List[ConfigNode] = _children

  def addChild[T <: ConfigNode](child: T): T = {
    if (_initialized) throw new IllegalStateException(s"ConfigGroup[$name] has been initialized")

    //检查名字是否合法
    require(
      child.name.matches("^[a-zA-Z0-9_\\-]+$"),
      s"Invalid config name: '${child.name}'. Allowed characters: letters (a-z, A-Z), digits (0-9), underscore (_), and hyphen (-)."
    )

    //检查是否已经有该名字的子节点
    if (_children.exists(_.name == child.name)) {
      throw new IllegalArgumentException(s"ConfigGroup[$name] already contains a child named \"${child.name}\"")
    }

    //给子节点设置父节点
    child match {
      case group: ConfigGroup => group.parent = Some(this)
      case item: ConfigItem[_] => item.parent = Some(this)
    }

    _children = _children :+ child
    child
  }

  override def test(t: Regex): Boolean = {
    t.findFirstIn(this.name).isDefined
      || children.exists(_.test(t))
  }

  override def serialization: SerializeElement = SerializeObject.build {
    children.foreach { c =>
      try c.name := c
      catch {
        case e: Throwable =>
          val s = new SerializationException(s"Config[${c.name}] serialization failed", e)
          root.fold(ExceptionHandler.Throw.onSerializationException(c, s))(_.exceptionHandler.onSerializationException(c, s))
      }
    }
  }

  override def deserialization(data: SerializeElement): Unit = {
    data match {
      case obj: SerializeObject =>
        children.foreach { c =>
          obj.get(c.name).foreach { element =>
            try c.deserialization(element)
            catch {
              case e: Throwable =>
                root.foreach(_.markSavable())
                val s = new DeserializationException(s"Config[${c.name}] deserialization failed", e)
                root.fold(ExceptionHandler.Throw.onDeserializationException(c, s))(_.exceptionHandler.onDeserializationException(c, s))
            }
          }
        }
      case _ =>
        root.foreach(_.markSavable())
        val s = new DeserializationException(s"Config[${this.name}] deserialization failed : error data type, require SerializeObject,but find ${data.getClass.getName}")
        root.fold(ExceptionHandler.Throw.onDeserializationException(this, s))(_.exceptionHandler.onDeserializationException(this, s))
    }
  }

}


object ConfigGroup {

  extension (self: ConfigGroup) {

    def items: List[ConfigItem[?]] = self.children.collect { case item: ConfigItem[_] => item }

    def groups: List[ConfigGroup] = self.children.collect { case group: ConfigGroup => group }

    def flat: List[ConfigNode] = self :: self.children.flatMap {
      case group: ConfigGroup => group.flat
      case item: ConfigItem[_] => List(item)
    }

    def findNode(path: String): Option[ConfigNode] = {
      if (path.isEmpty) return Some(self)

      val segments = path.split('.').toList

      segments.foldLeft(Option[ConfigNode](self)) { (currentNode, name) =>
        currentNode.flatMap {
          case g: ConfigGroup => g.children.find(_.name == name)
          case _ => None
        }
      }
    }

    private def getDeclaredNodes = self.getClass.getDeclaredFields.filter { field =>
      classOf[ConfigNode].isAssignableFrom(field.getType) && field.getName != "MODULE$"
    }

    private def getNode(field: Field) = {
      //如果是object 通过内部的MODULE$获取实例
      val moduleFiled = try field.getType.getDeclaredField("MODULE$")
      catch case e: Throwable => null
      if (moduleFiled != null) {
        moduleFiled.setAccessible(true)
        moduleFiled.get(null).asInstanceOf[ConfigNode]
      } else {
        field.get(self).asInstanceOf[ConfigNode]
      }
    }

    private def discoverAndRegisterNodes(): Unit = {
      getDeclaredNodes.foreach { field =>
        field.setAccessible(true)
        val comment = field.getAnnotation(classOf[Comment])
        val node: ConfigNode = getNode(field)
        if (node != null) {
          if (comment != null) {
            node.comment(comment.text)
          }
          if (!self.children.exists(c => c.name == node.name && c == node)) {
            self.addChild(node)
          }
        }
      }
    }

  }


}
