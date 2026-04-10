package moe.forpleuvoir.nebula.config.item

import moe.forpleuvoir.nebula.config.ConfigItem
import moe.forpleuvoir.nebula.serialization.base.SerializeObject.:=
import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializeObject}
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.Codec.{deserialization, serialization}

import scala.collection.mutable

class ConfigMap[V](
  name: String,
  defaultValue: Map[String, V],
  codec: Codec[V]
) extends ConfigItem[Map[String, V]](name, defaultValue), mutable.Map[String, V] {

  given Codec[V] = codec

  private val map: mutable.Map[String, V] = mutable.LinkedHashMap.from(defaultValue)

  override def equals(o: Any): Boolean = o match {
    case that: ConfigMap[?] =>
      this.map == that.map && this.name == that.name
    case _ => false
  }

  override def isDefault: Boolean = map == this.defaultValue

  override def getValue: Map[String, V] = map.toMap

  override def setValue(value: Map[String, V]): ConfigMap.this.type = {
    if (value != map) {
      map.clear()
      map.addAll(value)
      notifyChange()
    }
    this
  }

  override def serialization: SerializeElement = SerializeObject.build {
    map.foreachEntry((k, v) =>
      k := v.serialization
    )
  }

  override def deserialization(data: SerializeElement): Unit = {
    data match {
      case obj: SerializeObject =>
        val m = obj.map { case (k, v) => k -> v.deserialization[V].get }
        setValue(m.toMap)
      case _ =>
    }
  }

  override def addOne(elem: (String, V)): this.type = {
    map.addOne(elem)
    notifyChange()
    this
  }

  override def get(key: String): Option[V] = map.get(key)

  override def iterator: Iterator[(String, V)] = map.iterator

  override def subtractOne(elem: String): this.type = {
    map.subtractOne(elem)
    notifyChange()
    this
  }

}

object ConfigMap {
  def apply[V](
    name: String,
    defaultValue: Map[String, V]
  )(using codec: Codec[V]): ConfigMap[V] = new ConfigMap[V](name, defaultValue, codec)

  def of[V](
    name: String,
    defaultValue: (String, V)*
  )(using codec: Codec[V]): ConfigMap[V] = new ConfigMap[V](name, defaultValue.toMap, codec)
}
