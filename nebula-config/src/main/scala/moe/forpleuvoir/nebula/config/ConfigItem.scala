package moe.forpleuvoir.nebula.config

import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.codec.Codec

import scala.util.matching.Regex

trait ConfigItem[C](
  override val name: String,
  override val defaultValue: C,
) extends ConfigNode, ConfigValued[C] {

  protected var _value: C = defaultValue

  override def initialization(): Unit = {}

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

class Config[C](
  name: String,
  defaultValue: C,
  codec: Codec[C],
) extends ConfigItem[C](name, defaultValue) {

  override def deserialization(data: SerializeElement): Unit =
    codec.deserialization(data).foreach(value => this.setValue(value))

  override def serialization: SerializeElement = codec.serialization(getValue)

}

object Config {
  def apply[C](
    name: String,
    defaultValue: C,
  )(using codec: Codec[C]): Config[C] = new Config[C](name, defaultValue, codec)
}