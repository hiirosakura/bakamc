package moe.forpleuvoir.nebula.common.color

import moe.forpleuvoir.nebula.common.util.lerp
import moe.forpleuvoir.nebula.common.util.primitive.CoerceInExtension.coerceIn

trait RGBColor {

  def red: Int

  def redF: Float

  def green: Int

  def greenF: Float

  def blue: Int

  def blueF: Float

  def rgb: Int

  def hexString: String = f"$rgb%06X"

  def copy: RGBColor

  def reverse: RGBColor = Color.ofARGB(255 - red, 255 - green, 255 - blue)

  def lerp(to: RGBColor, fraction: Float): RGBColor = Color.ofARGB(
    redF.lerp(to.redF, fraction),
    greenF.lerp(to.greenF, fraction),
    blueF.lerp(to.blueF, fraction)
  )

  infix def +(other: RGBColor): RGBColor = Color.ofARGB(
    (red + other.red).coerceIn(0, 255),
    (green + other.green).coerceIn(0, 255),
    (blue + other.blue).coerceIn(0, 255)
  )

  infix def -(other: RGBColor): RGBColor = Color.ofARGB(
    (red - other.red).coerceIn(0, 255),
    (green - other.green).coerceIn(0, 255),
    (blue - other.blue).coerceIn(0, 255)
  )

  infix def *(other: RGBColor): RGBColor = Color.ofARGB(
    (redF * other.redF).coerceIn(0, 1f),
    (greenF * other.greenF).coerceIn(0, 1f),
    (blueF * other.blueF).coerceIn(0, 1f)
  )

  infix def /(other: RGBColor): RGBColor = Color.ofARGB(
    (redF / other.redF).coerceIn(0, 1f),
    (greenF / other.greenF).coerceIn(0, 1f),
    (blueF / other.blueF).coerceIn(0, 1f)
  )

}
