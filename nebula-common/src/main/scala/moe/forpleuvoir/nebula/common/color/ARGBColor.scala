package moe.forpleuvoir.nebula.common.color

import moe.forpleuvoir.nebula.common.util.lerp
import moe.forpleuvoir.nebula.common.util.primitive.CoerceInExtension.coerceIn

trait ARGBColor extends RGBColor {

  def alpha: Int

  def alphaF: Float

  def argb: Int

  override def hexString: String = f"$argb%08X"

  override def copy: ARGBColor

  override def reverse: ARGBColor = Color.ofARGB(
    255 - red,
    255 - green,
    255 - blue,
    alpha
  )

  def lerp(to: ARGBColor, fraction: Float): ARGBColor = Color.ofARGB(
    redF.lerp(to.redF, fraction),
    greenF.lerp(to.greenF, fraction),
    blueF.lerp(to.blueF, fraction),
    alphaF.lerp(to.alphaF, fraction)
  )

  infix def +(other: ARGBColor): ARGBColor = Color.ofARGB(
    (red + other.red).coerceIn(0, 255),
    (green + other.green).coerceIn(0, 255),
    (blue + other.blue).coerceIn(0, 255),
    (alpha + other.alpha).coerceIn(0, 255)
  )

  infix def -(other: ARGBColor): ARGBColor = Color.ofARGB(
    (red - other.red).coerceIn(0, 255),
    (green - other.green).coerceIn(0, 255),
    (blue - other.blue).coerceIn(0, 255),
    (alpha - other.alpha).coerceIn(0, 255)
  )

  infix def *(other: ARGBColor): ARGBColor = Color.ofARGB(
    (redF * other.redF).coerceIn(0f, 1f),
    (greenF * other.greenF).coerceIn(0f, 1f),
    (blueF * other.blueF).coerceIn(0f, 1f),
    (alphaF * other.alphaF).coerceIn(0f, 1f)
  )

  infix def /(other: ARGBColor): ARGBColor = Color.ofARGB(
    (redF / other.redF).coerceIn(0f, 1f),
    (greenF / other.greenF).coerceIn(0f, 1f),
    (blueF / other.blueF).coerceIn(0f, 1f),
    (alphaF / other.alphaF).coerceIn(0f, 1f)
  )

}
