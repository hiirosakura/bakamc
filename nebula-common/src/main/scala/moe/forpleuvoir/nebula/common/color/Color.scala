package moe.forpleuvoir.nebula.common.color

import moe.forpleuvoir.nebula.common.util.lerp as valueLerp
import moe.forpleuvoir.nebula.common.util.primitive.CoerceInExtension.clamp

import java.util.concurrent.ConcurrentHashMap

private object HSVHelper {

  /**
   * RGB颜色转HSV
   *
   * @param r red
   * @param g green
   * @param b blue
   * @return (hue range 0-360, saturation range 0-100, value range 0-100)
   */
  private[color] def rgbToHSV(r: Int, g: Int, b: Int): (Float, Float, Float) = {
    val hsv = Array(0.0f, 0.0f, 0.0f)
    java.awt.Color.RGBtoHSB(r, g, b, hsv)
    val hue = hsv(0).clamp(0.0f, 1.0f)
    val saturation = hsv(1).clamp(0.0f, 1f)
    val value = hsv(2).clamp(0.0f, 1f)
    (hue * 360f, saturation * 100f, value * 100f)
  }

  /**
   * HSV颜色转RGB
   *
   * @param h hue range 0-360
   * @param s saturation range 0-100
   * @param v value range 0-100
   * @return
   */
  private[color] def hsvToRGB(h: Float, s: Float, v: Float): Int = {
    val hue = h.clamp(0f, 360f) / 360f
    val saturation = s.clamp(0.0f, 100.0f) / 100f
    val value = v.clamp(0.0f, 100.0f) / 100f
    java.awt.Color.HSBtoRGB(hue, saturation, value)
  }

  private val H_FACTOR = 100.0f

  private val SV_FACTOR = 1000.0f

  private val MASK_20 = 0xFFFFF

  /**
   * HSV颜色缓存
   * 缓存键为颜色值，缓存值为色相，饱和度，明度
   */
  private val cache = new ConcurrentHashMap[Color, Long](1000)

  private def pack(h: Float, s: Float, v: Float): Long = {
    val hLong = (h * H_FACTOR).toLong & MASK_20
    val sLong = (s * SV_FACTOR).toLong & MASK_20
    val vLong = (v * SV_FACTOR).toLong & MASK_20
    (hLong << 40) | (sLong << 20) | vLong
  }

  private def unpack(packed: Long): (Float, Float, Float) = {
    val h = unpackHue(packed)
    val s = unpackSaturation(packed)
    val v = unpackValue(packed)
    (h, s, v)
  }

  /**
   * 更新 Hue (H): 输入旧的 Long 和新的 h (0-360)，返回更新后的 Long
   */
  private def updateHue(oldPacked: Long, newH: Float): Long = {
    val safeH = newH.clamp(0.0f, 360.0f)
    val hLong = (safeH * H_FACTOR).toLong & MASK_20
    (oldPacked & ~(MASK_20 << 40)) | (hLong << 40)
  }

  /**
   * 更新 Saturation (S): 范围 0.0 - 100.0
   * 占据中间 20 位 (20-39)
   */
  private def updateSaturation(oldPacked: Long, newS: Float): Long = {
    val safeS = newS.clamp(0.0f, 100.0f)
    val sLong = (safeS * SV_FACTOR).toLong & MASK_20
    (oldPacked & ~(MASK_20 << 20)) | (sLong << 20)
  }

  /**
   * 更新 Value (V): 范围 0.0 - 100.0
   * 占据最低 20 位 (0-19)
   */
  private def updateValue(oldPacked: Long, newV: Float): Long = {
    val safeV = newV.clamp(0.0f, 100.0f)
    val vLong = (safeV * SV_FACTOR).toLong & MASK_20
    (oldPacked & ~MASK_20) | vLong
  }

  /**
   * 解包 Hue: 返回 0-360 范围的 Float
   */
  private inline def unpackHue(value: Long): Float =
    ((value >> 40) & MASK_20) / H_FACTOR

  /**
   * 解包 Saturation: 返回 0.0 - 100.0
   */
  private inline def unpackSaturation(value: Long): Float =
    ((value >> 20) & MASK_20) / SV_FACTOR

  /**
   * 解包 Value: 返回 0.0 - 100.0
   */
  private inline def unpackValue(value: Long): Float =
    (value & MASK_20) / SV_FACTOR

  private def getOrPutHSV(color: Color): Long =
    cache.computeIfAbsent(color, { c =>
      val (h, s, v) = rgbToHSV(c.red, c.green, c.blue)
      pack(h, s, v)
    })

  def cache(color: Color): Long = getOrPutHSV(color)

  def cache(color: Color, hue: Float, saturation: Float, value: Float): Long =
    cache.putIfAbsent(color, pack(hue, saturation, value))

  def getHue(color: Color): Float =
    unpackHue(getOrPutHSV(color))

  def setHue(color: Color, newH: Float): Color = {
    val hsv = getOrPutHSV(color)
    val newPack = updateHue(hsv, newH)
    val s = unpackSaturation(newPack)
    val v = unpackValue(newPack)
    val result = color.rgb(hsvToRGB(newH, s, v))
    cache.putIfAbsent(result, newPack)
    result
  }

  def getSaturation(color: Color): Float =
    unpackSaturation(getOrPutHSV(color))

  def setSaturation(color: Color, newS: Float): Color = {
    val hsv = getOrPutHSV(color)
    val newPack = updateSaturation(hsv, newS)
    val h = unpackHue(newPack)
    val v = unpackValue(newPack)
    val result = color.rgb(hsvToRGB(h, newS, v))
    cache.putIfAbsent(result, newPack)
    result
  }

  def getValue(color: Color): Float =
    unpackValue(getOrPutHSV(color))

  def setValue(color: Color, newV: Float): Color = {
    val hsv = getOrPutHSV(color)
    val newPack = updateValue(hsv, newV)
    val h = unpackHue(newPack)
    val s = unpackSaturation(newPack)
    val result = color.rgb(hsvToRGB(h, s, newV))
    cache.putIfAbsent(result, newPack)
    result
  }

}

opaque type Color = Int

object Color {

  private def hexToInt(hex: String): Int = {
    val str = hex.replaceAll("0x|0X|#", "")
    str.length match {
      case 8 => Integer.parseInt(str, 16)
      case 6 => 0xFF000000 | Integer.parseInt(str, 16)
      case _ => throw new IllegalArgumentException(s"Unable to parse color information from [$hex]")
    }
  }

  private def normalize(value: Int | Float | Double)(name: String): Int =
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

  private def normalizeSV(value: Int | Float | Double)(name: String): Float =
    value match {
      case i: Int =>
        require(i >= 0 && i <= 100, s"$name value $i is out of range 0-100")
        i.toFloat
      case f: Float =>
        require(f >= 0.0f && f <= 100f, s"$name value $f is out of range 0.0-100.0")
        f
      case d: Double =>
        require(d >= 0.0 && d <= 100f, s"$name value $d is out of range 0.0-100.0")
        d.toFloat
    }

  private def normalizeHue(value: Int | Float | Double): Float =
    value match {
      case i: Int =>
        require(i >= 0 && i <= 360, s"hue value $i is out of range 0-360")
        i.toFloat
      case f: Float =>
        require(f >= 0.0f && f <= 360f, s"hue value $f is out of range 0.0-360.0")
        f
      case d: Double =>
        require(d >= 0.0 && d <= 360f, s"hue value $d is out of range 0.0-360.0")
        d.toFloat
    }

  def fromARGB(
    red: Int | Float | Double,
    green: Int | Float | Double,
    blue: Int | Float | Double,
    alpha: Int | Float | Double = 255
  ): Color = {
    val r = normalize(red)("red")
    val g = normalize(green)("green")
    val b = normalize(blue)("blue")
    val a = normalize(alpha)("alpha")
    (a << 24) | (r << 16) | (g << 8) | b
  }

  def fromARGB(argbValue: Int): Color = argbValue

  def fromRGB(rgbValue: Int): Color = rgbValue.alpha(255)

  def fromHSV(
    hue: Int | Float | Double,
    saturation: Int | Float | Double,
    value: Int | Float | Double,
    alpha: Int | Float | Double = 255
  ): Color = {
    val h = normalizeHue(hue)
    val s = normalizeSV(saturation)("saturation")
    val v = normalizeSV(value)("value")
    val color = HSVHelper.hsvToRGB(h, s, v).alpha(alpha)
    HSVHelper.cache(color, h, s, v)
    color
  }

  def fromHex(hex: String): Color = hexToInt(hex)

  //基础分量
  extension (color: Color) {

    def toHex: String = {
      if (alpha == 255)
        f"#${color & 0xFFFFFF}%06X"
      else
        f"#$color%08X"
    }

    def asString: String =
      s"Color(hex: ${color.toHex}, alpha: ${color.alpha}, rgb: (${color.red}, ${color.green}, ${color.blue}), hsv: (${HSVHelper.getHue(color)}, ${HSVHelper.getSaturation(color)}, ${HSVHelper.getValue(color)}))"

    //region Alpha

    def alpha: Int = (color >> 24) & 0xFF

    def alphaF: Float = alpha / 255.0f

    def alpha(alpha: Int | Float | Double): Color =
      (normalize(alpha)("alpha") << 24) | color & 0x00FFFFFF

    //endregion

    //region Red

    def red: Int = (color >> 16) & 0xFF

    def redF: Float = red / 255.0f

    def red(red: Int | Float | Double): Color =
      (normalize(red)("red") << 16) | color & 0xFF00FFFF

    //endregion

    //region Green

    def green: Int = (color >> 8) & 0xFF

    def greenF: Float = green / 255.0f

    def green(green: Int | Float | Double): Color =
      (normalize(green)("green") << 8) | color & 0xFFFF00FF

    //endregion

    //region Blue

    def blue: Int = color & 0xFF

    def blueF: Float = blue / 255.0f

    def blue(blue: Int | Float | Double): Color =
      normalize(blue)("blue") | color & 0xFFFFFF00

    //endregion

    //region RGB

    def rgb: Int = color & 0x00FFFFFF

    def rgb(rgb: Int): Color = (color & 0xFF000000) | (rgb & 0x00FFFFFF)

    //endregion

    //region Hue
    /**
     * 色相 range 0.0-360.0
     *
     * @return
     */
    def hue: Float = HSVHelper.getHue(color)

    /**
     * 设置色相
     *
     * @param hue 色相 intRange 0-360,floatRange,doubleRange 0.0-360.0
     * @return
     */
    def hue(hue: Int | Float | Double): Color = {
      val _h = hue match {
        case i: Int =>
          require(i >= 0 && i <= 360, s"hue value $i is out of range 0-360")
          i.toFloat
        case f: Float =>
          require(f >= 0.0f && f <= 360f, s"hue value $f is out of range 0.0-360.0")
          f
        case d: Double =>
          require(d >= 0.0 && d <= 360.0, s"hue value $d is out of range 0.0-360.0")
          d.toFloat
      }
      HSVHelper.setValue(color, _h)
    }
    //endregion

    //region Saturation
    /**
     * 饱和度 range 0.0-100.0
     *
     * @return
     */
    def saturation: Float = HSVHelper.getSaturation(color)

    /**
     * 设置饱和度
     *
     * @param saturation 饱和度 intRange 0-100,floatRange,doubleRange 0.0-100.0
     * @return
     */
    def saturation(saturation: Int | Float | Double): Color = {
      val _s = saturation match {
        case i: Int =>
          require(i >= 0 && i <= 100, s"saturation value $i is out of range 0-100")
          i.toFloat
        case f: Float =>
          require(f >= 0.0f && f <= 100f, s"saturation value $f is out of range 0.0-100.0")
          f
        case d: Double =>
          require(d >= 0.0 && d <= 100.0, s"saturation value $d is out of range 0.0-100.0")
          d.toFloat
      }
      HSVHelper.setSaturation(color, _s)
    }
    //endregion

    //region Value
    /**
     * 明度 range 0.0-100.0
     *
     * @return
     */
    def value: Float = HSVHelper.getValue(color)

    /**
     * 明度
     *
     * @param value 明度 intRange 0-100,floatRange,doubleRange 0.0-100.0
     * @return
     */
    def value(value: Int | Float | Double): Color = {
      val _v = value match {
        case i: Int =>
          require(i >= 0 && i <= 100, s"value value $i is out of range 0-100")
          i.toFloat
        case f: Float =>
          require(f >= 0.0f && f <= 100f, s"value value $f is out of range 0.0-100.0")
          f
        case d: Double =>
          require(d >= 0.0 && d <= 100.0, s"value value $d is out of range 0.0-100.0")
          d.toFloat
      }
      HSVHelper.setValue(color, _v)
    }
    //endregion

  }

  extension (color: Color) {

    /**
     * 使用RGB分量进行线性插值
     *
     * @param target   目标颜色
     * @param fraction 插值比例 range 0.0-1.0
     * @param alpha    是否对`alpha`进行插值
     * @return
     */
    def lerp(target: Color, fraction: Float, alpha: Boolean = false): Color = {
      val red = color.red.valueLerp(target.red, fraction)
      val green = color.green.valueLerp(target.green, fraction)
      val blue = color.blue.valueLerp(target.blue, fraction)
      val a = if (alpha) color.alpha.valueLerp(target.alpha, fraction) else color.alpha
      Color.fromARGB(red, green, blue, a)
    }

    /**
     * 使用HSV分量进行线性插值
     *
     * @param target   插值目标
     * @param fraction 插值比例 range 0.0-1.0
     * @param alpha    是否对`alpha`进行插值
     * @return
     */
    def hsvLerp(target: Color, fraction: Float, alpha: Boolean = false): Color = {
      val h = color.hue.valueLerp(target.hue, fraction)
      val s = color.saturation.valueLerp(target.saturation, fraction)
      val v = color.value.valueLerp(target.value, fraction)
      val a = if (alpha) color.alpha.valueLerp(target.alpha, fraction) else color.alpha
      Color.fromHSV(h, s, v, a)
    }

    /**
     * 反色
     *
     * @param alpha 是否对`alpha`进行反转
     * @return
     */
    def reverse(alpha: Boolean = false): Color = {
      val red = 255 - color.red
      val green = 255 - color.green
      val blue = 255 - color.blue
      val a = if (alpha) 255 - color.alpha else color.alpha
      Color.fromARGB(red, green, blue, a)
    }

    infix def +(other: Color): Color = {
      val red = (color.red + other.red).clamp(0, 255)
      val green = (color.green + other.green).clamp(0, 255)
      val blue = (color.blue + other.blue).clamp(0, 255)
      val a = (color.alpha + other.alpha).clamp(0, 255)
      Color.fromARGB(red, green, blue, a)
    }

    infix def -(other: Color): Color = {
      val red = (color.red - other.red).clamp(0, 255)
      val green = (color.green - other.green).clamp(0, 255)
      val blue = (color.blue - other.blue).clamp(0, 255)
      val a = (color.alpha - other.alpha).clamp(0, 255)
      Color.fromARGB(red, green, blue, a)
    }

    infix def *(other: Color): Color = {
      val red = (color.redF * other.redF).clamp(0, 1)
      val green = (color.greenF * other.greenF).clamp(0, 1)
      val blue = (color.blueF * other.blueF).clamp(0, 1)
      val a = (color.alphaF * other.alphaF).clamp(0, 1)
      Color.fromARGB(red, green, blue, a)
    }

    infix def /(other: Color): Color = {
      val red = (color.redF / other.redF).clamp(0, 1)
      val green = (color.greenF / other.greenF).clamp(0, 1)
      val blue = (color.blueF / other.blueF).clamp(0, 1)
      val a = (color.alphaF / other.alphaF).clamp(0, 1)
      Color.fromARGB(red, green, blue, a)
    }

  }

}