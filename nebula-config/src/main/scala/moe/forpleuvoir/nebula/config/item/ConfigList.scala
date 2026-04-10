package moe.forpleuvoir.nebula.config.item

import moe.forpleuvoir.nebula.config.ConfigItem
import moe.forpleuvoir.nebula.serialization.base.{SerializeArray, SerializeElement}
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.Codec.deserialization

import scala.collection.mutable

class ConfigList[T](
  name: String,
  defaultValue: Seq[T],
  codec: Codec[T]
) extends ConfigItem[Seq[T]](name, defaultValue), mutable.Buffer[T] {

  given Codec[T] = codec

  private val buffer = mutable.Buffer.from(defaultValue)

  override def isDefault: Boolean = buffer == this.defaultValue

  override def equals(o: Any): Boolean = o match {
    case list: ConfigList[?] =>
      list.buffer == buffer && name == list.name
    case _ => false
  }

  override def getValue: Seq[T] = buffer.toSeq

  override def setValue(value: Seq[T]): ConfigList.this.type = {
    if (buffer != value) {
      buffer.clear()
      buffer.addAll(value)
      notifyChange()
    }
    this
  }

  override def serialization: SerializeElement = SerializeArray.create(getValue *)

  override def deserialization(data: SerializeElement): Unit = {
    data match {
      case array: SerializeArray =>
        setValue(array.map(_.deserialization[T].get).toSeq)
      case _ => throw IllegalStateException("Not a array")
    }
  }

  override def prepend(elem: T): this.type = {
    buffer.prepend(elem)
    notifyChange()
    this
  }

  override def insert(idx: Int, elem: T): Unit = {
    buffer.insert(idx, elem)
    notifyChange()
  }

  override def insertAll(idx: Int, elems: IterableOnce[T]): Unit = {
    buffer.insertAll(idx, elems)
    notifyChange()
  }

  override def remove(idx: Int): T = {
    val r = buffer.remove(idx)
    notifyChange()
    r
  }

  override def remove(idx: Int, count: Int): Unit = {
    buffer.remove(idx, count)
    notifyChange()
  }

  override def patchInPlace(from: Int, patch: IterableOnce[T], replaced: Int): this.type = {
    buffer.patchInPlace(from, patch, replaced)
    notifyChange()
    this
  }

  override def addOne(elem: T): this.type = {
    buffer.addOne(elem)
    notifyChange()
    this
  }

  override def clear(): Unit = {
    buffer.clear()
    notifyChange()
  }

  override def update(idx: Int, elem: T): Unit = {
    buffer.update(idx, elem)
    notifyChange()
  }

  override def apply(i: Int): T = buffer(i)

  override def length: Int = buffer.length

  override def iterator: Iterator[T] = buffer.iterator


}

object ConfigList {
  def apply[T](name: String, defaultValue: Seq[T])(using codec: Codec[T]): ConfigList[T] = new ConfigList[T](name, defaultValue, codec)

  def of[T](name: String, defaultValue: T*)(using codec: Codec[T]): ConfigList[T] = new ConfigList[T](name, defaultValue.toSeq, codec)
}