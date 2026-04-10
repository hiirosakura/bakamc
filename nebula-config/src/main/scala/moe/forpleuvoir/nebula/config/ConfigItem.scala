package moe.forpleuvoir.nebula.config

import scala.util.matching.Regex


trait ConfigItem[C](
  override val name: String,
  override val defaultValue: C,
) extends ConfigNode, ConfigValued[C] {

  protected var _value: C = defaultValue

  override def init(): Unit = {}

  override def test(t: Regex): Boolean = {
    t.findFirstIn(this.name).isDefined || t.findFirstIn(this.value.toString).isDefined
  }

  private var _parent: Option[ConfigGroup] = None

  override def parent: Option[ConfigGroup] = _parent

  private[config] def parent_=(parent: Option[ConfigGroup]): Unit = _parent = parent

  private var _observers: List[this.type => Unit] = Nil

  override def observe(callback: this.type => Unit): Unit = {
    _observers = _observers :+ callback
  }

  override def notifyChange(): Unit = {
    root.foreach(_.markSavable())
    _observers.foreach(callback => callback(this))
  }


}
