package moe.forpleuvoir.nebula.common.color

import moe.forpleuvoir.nebula.common.color.Color.normalize
import moe.forpleuvoir.nebula.common.util.primitive.CoerceInExtension.coerceIn

case class Color private(private var value: Int) extends ARGBColor {

  override def alpha: Int = (value >>> 24) & 0xFF

  override def alphaF: Float = alpha / 255.0f

  def alpha(value: Int | Float | Double): Color = {
    this.value = (normalize(value)("alpha") << 24) | (this.value & 0x00FFFFFF)
    this
  }

  override def red: Int = (value >>> 16) & 0xFF

  override def redF: Float = red / 255.0f

  def red(value: Int | Float | Double): Color = {
    this.value = (this.value & 0xFF00FFFF) | (normalize(value)("red") << 16)
    this
  }

  override def green: Int = (value >>> 8) & 0xFF

  override def greenF: Float = green / 255.0f

  def green(value: Int | Float | Double): Color = {
    this.value = (this.value & 0xFFFF00FF) | (normalize(value)("green") << 8)
    this
  }

  override def blue: Int = value & 0xFF

  override def blueF: Float = blue / 255.0f

  def blue(value: Int | Float | Double): Color = {
    this.value = (this.value & 0xFFFFFF00) | normalize(value)("blue")
    this
  }

  override def rgb: Int = value & 0x00FFFFFF

  def rgb(value: Int): Color = {
    this.value = (this.value & 0xFF000000) | (value & 0x00FFFFFF)
    this
  }

  override def argb: Int = value

  def argb(value: Int): Color = {
    this.value = value
    this
  }

  override def toString: String = f"Color(hex: #$hexString,red: $red, green: $green, blue: $blue, alpha: $alpha)"

  override def clone(): Color = Color.ofARGB(this.value)

  override def copy: Color = this.clone()

  infix def +=(other: RGBColor | ARGBColor): Color = {
    red((this.red + other.red).coerceIn(0, 255))
    green((this.green + other.green).coerceIn(0, 255))
    blue((this.blue + other.blue).coerceIn(0, 255))
    other match {
      case argb: ARGBColor =>
        this.alpha((alpha + argb.alpha).coerceIn(0, 255))
      case _: RGBColor => this
    }
  }

  infix def -=(other: RGBColor | ARGBColor): Color = {
    red((this.red - other.red).coerceIn(0, 255))
    green((this.green - other.green).coerceIn(0, 255))
    blue((this.blue - other.blue).coerceIn(0, 255))
    other match {
      case argb: ARGBColor =>
        this.alpha((alpha - argb.alpha).coerceIn(0, 255))
      case _: RGBColor => this
    }
  }

  infix def *=(other: RGBColor | ARGBColor): Color = {
    red((this.redF * other.redF).coerceIn(0, 1))
    green((this.greenF * other.greenF).coerceIn(0, 1))
    blue((this.blueF * other.blueF).coerceIn(0, 1))
    other match {
      case argb: ARGBColor =>
        this.alpha((alphaF * argb.alphaF).coerceIn(0, 1))
      case _: RGBColor => this
    }
  }

  infix def /=(other: RGBColor | ARGBColor): Color = {
    red((this.redF / other.redF).coerceIn(0, 1))
    green((this.greenF / other.greenF).coerceIn(0, 1))
    blue((this.blueF / other.blueF).coerceIn(0, 1))
    other match {
      case argb: ARGBColor =>
        this.alpha((alphaF / argb.alphaF).coerceIn(0, 1))
      case _: RGBColor => this
    }
  }

}

extension (color: Color) {

  def withAlpha(alpha: Int | Float | Double): Color = Color.ofARGB(color.argb).alpha(alpha)

  def withRed(red: Int | Float | Double): Color = Color.ofARGB(color.argb).red(red)

  def withGreen(green: Int | Float | Double): Color = Color.ofARGB(color.argb).green(green)

  def withBlue(blue: Int | Float | Double): Color = Color.ofARGB(color.argb).blue(blue)

  def withRGB(rgb: Int): Color = Color.ofARGB(color.argb).rgb(rgb)

  def withOpacity(opacity: Int | Float | Double): Color = Color.ofARGB(color.argb).alpha(opacity)

}

object Color {

  def ofARGB(argb: Int) = new Color(argb)

  def ofARGB(
    red: Int | Float | Double = 255,
    green: Int | Float | Double = 255,
    blue: Int | Float | Double = 255,
    alpha: Int | Float | Double = 255
  ): Color = {
    val r = normalize(red)("red")
    val g = normalize(green)("green")
    val b = normalize(blue)("blue")
    val a = normalize(alpha)("alpha")
    new Color((a << 24) | (r << 16) | (g << 8) | b)
  }

  def ofRGB(rgb: Int) = new Color(rgb | 0xFF000000)

  def ofRGB(
    red: Int | Float | Double = 255,
    green: Int | Float | Double = 255,
    blue: Int | Float | Double = 255
  ): Color = Color.ofARGB(red, green, blue)

  def ofStr(hex: String) = new Color(hexToInt(hex))

  private def hexToInt(hex: String): Int = {
    val str = hex.replaceAll("0x|0X|#", "")
    str.length match {
      case 8 => Integer.parseInt(str, 16)
      case 6 => 0xFF000000 | Integer.parseInt(str, 16)
      case _ => throw new IllegalArgumentException(s"Unable to parse color information from [$hex]")
    }
  }

  private def normalize(value: Int | Float | Double)(name: String): Int = {
    value match {
      case i: Int =>
        require(i >= 0 && i <= 255, s"$name value $i is out of range 0-255")
        i
      case f: Float =>
        require(f >= 0.0f && f <= 1.0f, s"$name value $f is out of range 0.0-1.0")
        (f * 255).toInt
      case d: Double =>
        require(d >= 0.0 && d <= 1.0, s"$name value $d is out of range 0.0-1.0")
        (d * 255).toInt
    }
  }

}