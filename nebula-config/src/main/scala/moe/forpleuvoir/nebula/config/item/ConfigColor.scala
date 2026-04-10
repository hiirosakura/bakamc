package moe.forpleuvoir.nebula.config.item

import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.config.ConfigItem
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.codec.ColorCodec

class ConfigColor(
  name: String,
  defaultValue: Color
) extends ConfigItem[Color](name, defaultValue) {

  override def deserialization(data: SerializeElement): Unit = {
    ColorCodec.deserialization(data).foreach { value => setValue(value) }
  }

  override def serialization: SerializeElement = ColorCodec.serialization(getValue)
}

object ConfigColor {
  def apply(name: String, defaultValue: Color): ConfigColor = new ConfigColor(name, defaultValue)
}