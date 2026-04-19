package moe.forpleuvoir.nebula.config.item

import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.config.Config
import moe.forpleuvoir.nebula.serialization.codec.ColorCodec

class ConfigColor(
  name: String,
  defaultValue: Color
) extends Config[Color](name, defaultValue, ColorCodec)

object ConfigColor {
  def apply(name: String, defaultValue: Color): ConfigColor = new ConfigColor(name, defaultValue)
}