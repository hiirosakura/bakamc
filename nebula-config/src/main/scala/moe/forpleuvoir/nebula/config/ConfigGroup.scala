package moe.forpleuvoir.nebula.config

import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializeObject}
import moe.forpleuvoir.nebula.serialization.extension.SerObjectOps.:=

import java.lang.reflect.Field
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

  override def init(): Unit = {
    require(!_initialized, "ConfigGroup has been initialized")
    this.discoverAndRegisterNodes()
    _children.foreach(_.init())
    _initialized = true
  }

  def children: List[ConfigNode] = _children

  def addChild[T <: ConfigNode](child: T): T = {
    if (_initialized) throw new IllegalStateException(s"ConfigGroup[$name] has been initialized")
    child match {
      case group: ConfigGroup => group.parent = Some(this)
      case item: ConfigItem[_] => item.parent = Some(this)
    }
    _children = _children :+ child
    child
  }

  def items: List[ConfigItem[?]] = children.collect { case item: ConfigItem[_] => item }

  def groups: List[ConfigGroup] = children.collect { case group: ConfigGroup => group }

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

    def flat: List[ConfigNode] = self :: self.children.flatMap {
      case group: ConfigGroup => group.flat
      case item: ConfigItem[_] => List(item)
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
          if (!self.children.contains(node)) {
            self.addChild(node)
          }
        }
      }
    }

  }


}
