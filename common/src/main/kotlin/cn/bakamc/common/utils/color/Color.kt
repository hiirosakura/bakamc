package cn.bakamc.common.utils.color

import cn.bakamc.common.api.serialization.JsonSerializer
import cn.bakamc.common.utils.clamp
import cn.bakamc.common.utils.f
import cn.bakamc.common.utils.fillBefore
import cn.bakamc.common.utils.i
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

/**
 * 颜色

 * 项目名 BakaMC

 * 包名  cn.bakamc.common.utils.color

 * 文件名 Color

 * 创建时间 2022/8/8 22:17

 * @author forpleuvoir

 */
class Color : JsonSerializer {

	companion object {

		/**
		 * 解码字符串颜色 AARRGGBB 十六进制字符串
		 *
		 * 格式: 0xFFFFFFFF,0XFFFFFFFF,#FFFFFFFF
		 *
		 * @param color String
		 * @return Int
		 */
		@JvmStatic
		fun decode(color: String): Int {
			val hex: String = color.replace(Regex("0x|0X"), "").replace("#", "")
			return when (hex.length) {
				8    -> hex.toUInt(16).toInt()
				6    -> 0xFF000000.toInt() or hex.toUInt(16).toInt()
				else -> 0xFFFFFFFF.toInt()
			}
		}

		internal fun Int.fixValue(checkedRange: Boolean, parameterName: String, minValue: Int = 0, maxValue: Int = 255): Int {
			if ((this < minValue || this > maxValue) && checkedRange) {
				throw IllegalArgumentException("[$parameterName : $this]Color parameter outside of expected range[$minValue ~ $maxValue]")
			}
			return this.clamp(minValue, maxValue)
		}

		internal fun Float.fixValue(checkedRange: Boolean, parameterName: String, minValue: Float = 0.0F, maxValue: Float = 1.0F): Float {
			if ((this < minValue || this > maxValue) && checkedRange) {
				throw IllegalArgumentException("[$parameterName : $this]Color parameter outside of expected range[$minValue ~ $maxValue]")
			}
			return this.clamp(minValue, maxValue)
		}

		@JvmStatic
		val WHITE: Color get() = Color(255, 255, 255)

		@JvmStatic
		val LIGHT_GRAY: Color get() = Color(192, 192, 192)

		@JvmStatic
		val GRAY: Color get() = Color(128, 128, 128)

		@JvmStatic
		val DARK_GRAY: Color get() = Color(64, 64, 64)

		@JvmStatic
		val BLACK: Color get() = Color(0, 0, 0)

		@JvmStatic
		val RED: Color get() = Color(255, 0, 0)

		@JvmStatic
		val PINK: Color get() = Color(255, 175, 175)

		@JvmStatic
		val ORANGE: Color get() = Color(255, 200, 0)

		@JvmStatic
		val YELLOW: Color get() = Color(255, 255, 0)

		@JvmStatic
		val GREEN: Color get() = Color(0, 255, 0)

		@JvmStatic
		val MAGENTA: Color get() = Color(255, 0, 255)

		@JvmStatic
		val CYAN: Color get() = Color(0, 255, 255)

		@JvmStatic
		val BLUE: Color get() = Color(0, 0, 255)
	}

	/**
	 * 检查值得范围是否合法
	 */
	var checkedRange: Boolean

	/**
	 * @param argb [Int] 包含ARGB信息的颜色值
	 *
	 * @param checkedRange [Boolean]
	 *
	 * &#09;是否严格限制各种值是否合法
	 *
	 * &#09;如果为 true 则出现非法值会直接抛出异常[IllegalArgumentException]
	 *
	 * &#09;为 false 则只会将值修复到合法范围内
	 *
	 * @param fixed [Boolean] 是否为已经修复过的值
	 * @constructor
	 */
	private constructor(argb: Int, checkedRange: Boolean, fixed: Boolean) {
		this.argb = argb
		this.checkedRange = checkedRange
		if (!fixed) {
			red = red.fixValue(checkedRange, "Red")
			green = green.fixValue(checkedRange, "Green")
			blue = blue.fixValue(checkedRange, "Blue")
			alpha = alpha.fixValue(checkedRange, "Alpha")
		}
	}

	/**
	 * @param argb [Int] 包含ARGB信息的颜色值
	 *
	 * @param checkedRange [Boolean]
	 *
	 * &#09;是否严格限制各种值是否合法
	 *
	 * &#09;如果为 true 则出现非法值会直接抛出异常[IllegalArgumentException]
	 *
	 * &#09;为 false 则只会将值修复到合法范围内
	 *
	 * @constructor
	 */
	constructor(argb: Int, checkedRange: Boolean = false) : this(argb, checkedRange, false)

	/**
	 *
	 * @param red Int 红色值 Range(0 ~ 255)
	 * @param green Int 绿色值 Range(0 ~ 255)
	 * @param blue Int 蓝色值 Range(0 ~ 255)
	 * @param alpha Int 透明的 Range(0 ~ 255)
	 * @param checkedRange [Boolean]
	 *
	 * &#09;是否严格限制各种值是否合法
	 *
	 * &#09;如果为 true 则出现非法值会直接抛出异常[IllegalArgumentException]
	 *
	 * &#09;为 false 则只会将值修复到合法范围内
	 *
	 * @constructor
	 */
	constructor(red: Int = 255, green: Int = 255, blue: Int = 255, alpha: Int = 255, checkedRange: Boolean = false) : this(
		((alpha.fixValue(checkedRange, "Alpha") and 0xFF) shl 24) or
				((red.fixValue(checkedRange, "Red") and 0xFF) shl 16) or
				((green.fixValue(checkedRange, "Green") and 0xFF) shl 8) or
				((blue.fixValue(checkedRange, "Blue") and 0xFF)),
		checkedRange,
		true
	)

	/**
	 *
	 * @param red Int 红色值 Range(0.0F ~ 1.0F)
	 * @param green Int 绿色值 Range(0.0F ~ 1.0F)
	 * @param blue Int 蓝色值 Range(0.0F ~ 1.0F)
	 * @param alpha Int 透明的 Range(0.0F ~ 1.0F)
	 * @param checkedRange [Boolean]
	 *
	 * &#09;是否严格限制各种值是否合法
	 *
	 * &#09;如果为 true 则出现非法值会直接抛出异常[IllegalArgumentException]
	 *
	 * &#09;为 false 则只会将值修复到合法范围内
	 *
	 * @constructor
	 */
	constructor(red: Float = 1.0F, green: Float = 1.0F, blue: Float = 1.0F, alpha: Float = 1.0F, checkedRange: Boolean = false) : this(
		(red.fixValue(checkedRange, "Red") * 255).toInt(),
		(green.fixValue(checkedRange, "Green") * 255).toInt(),
		(blue.fixValue(checkedRange, "Blue") * 255).toInt(),
		(alpha.fixValue(checkedRange, "Alpha") * 255).toInt(),
		checkedRange
	)

	/**
	 * 获取颜色的ARGB信息
	 *
	 * Blue 0-7 bit
	 *
	 * Green 8-15 bit
	 *
	 * Red 16-23 bit
	 *
	 * Alpha 24-31 bit
	 */
	var argb: Int

	val hexStr: String get() = "#${argb.toUInt().toString(16).fillBefore(8, '0').uppercase()}"

	/**
	 * 红色值 Range(0 ~ 255)
	 */
	var red: Int
		get() = argb shr 16 and 0xFF
		set(value) {
			argb = (alpha and 0XFF shl 24) or (value.fixValue(checkedRange, "Red") and 0xFF shl 16) or (green and 0XFF shl 8) or (blue and 0XFF)
		}

	/**
	 * 红色值 Range(0.0F ~ 1.0F)
	 */
	var redF: Float
		get() = red.f / 255
		set(value) {
			red = (value.fixValue(checkedRange, "Red") * 255).i
		}


	/**
	 * 获取修改[red]之后的对象
	 * @param red [Int] Range(0 ~ 255)
	 * @return [Color] 原始对象
	 */
	fun red(red: Int): Color = this.apply { this.red = red }

	/**
	 * 获取修改[redF]之后的对象
	 * @param red [Float] Range(0.0F ~ 1.0F)
	 * @return [Color] 原始对象
	 */
	fun red(red: Float): Color = this.apply { this.redF = red }

	/**
	 * 绿色值 Range(0 ~ 255)
	 */
	var green: Int
		get() = argb shr 8 and 0xFF
		set(value) {
			argb = (alpha and 0XFF shl 24) or (red and 0xFF shl 16) or (value.fixValue(checkedRange, "Green") and 0XFF shl 8) or (blue and 0XFF)
		}

	/**
	 * 绿色值 Range(0.0F ~ 1.0F)
	 */
	var greenF: Float
		get() = green.f / 255
		set(value) {
			green = (value.fixValue(checkedRange, "Green") * 255).i
		}

	/**
	 * 获取修改[green]之后的对象
	 * @param green [Int] Range(0 ~ 255)
	 * @return [Color] 原始对象
	 */
	fun green(green: Int): Color = this.apply { this.green = green }

	/**
	 * 获取修改[greenF]之后的对象
	 * @param green [Float] Range(0.0F ~ 1.0F)
	 * @return [Color] 原始对象
	 */
	fun green(green: Float): Color = this.apply { this.greenF = green }

	/**
	 * 蓝色值 Range(0 ~ 255)
	 */
	var blue: Int
		get() = argb shr 0 and 0xFF
		set(value) {
			argb = (alpha and 0XFF shl 24) or (red and 0xFF shl 16) or (green and 0XFF shl 8) or (value.fixValue(checkedRange, "Blue") and 0XFF)
		}

	/**
	 * 蓝色值 Range(0.0F ~ 1.0F)
	 */
	var blueF: Float
		get() = blue.f / 255
		set(value) {
			blue = (value.fixValue(checkedRange, "Blue") * 255).i
		}

	/**
	 * 获取修改[blue]之后的对象
	 * @param blue [Int] Range(0 ~ 255)
	 * @return [Color] 原始对象
	 */
	fun blue(blue: Int): Color = this.apply { this.blue = blue }

	/**
	 * 获取修改[blueF]之后的对象
	 * @param blue [Float] Range(0.0F ~ 1.0F)
	 * @return [Color] 原始对象
	 */
	fun blue(blue: Float): Color = this.apply { this.blueF = blue }

	/**
	 * 不透明度 Range(0 ~ 255)
	 */
	var alpha: Int
		get() = argb shr 24 and 0xFF
		set(value) {
			argb = (value.fixValue(checkedRange, "Alpha") and 0XFF shl 24) or (red and 0xFF shl 16) or (green and 0XFF shl 8) or (blue and 0XFF)
		}

	/**
	 * 不透明度 Range(0.0F ~ 1.0F)
	 */
	var alphaF: Float
		get() = alpha.f / 255
		set(value) {
			alpha = (value.fixValue(checkedRange, "Alpha") * 255).i
		}

	/**
	 * 获取修改[alpha]之后的对象
	 * @param alpha [Int] Range(0 ~ 255)
	 * @return [Color] 原始对象
	 */
	fun alpha(alpha: Int): Color = this.apply { this.alpha = alpha }

	/**
	 * 获取修改[alpha]之后的对象
	 * @param alpha [Float] Range(0.0F ~ 1.0F)
	 * @return [Color] 原始对象
	 */
	fun alpha(alpha: Float): Color = this.apply { this.alphaF = alpha }

	/**
	 * 获取当前颜色的副本
	 * @return [Color] 复制对象
	 */
	fun copy(): Color = Color(argb)

	/**
	 * 获取调整不透明度之后的颜色复制对象
	 *
	 * 不透明度 = 当前不透明度 * opacity
	 *
	 * @param opacity [Float] Range(0.0F ~ 1.0F)
	 * @return [Color] 原始对象
	 */
	fun opacity(opacity: Float): Color = this.copy().apply { alphaF *= opacity.fixValue(checkedRange, "Opacity") }

	/**
	 * 获取调整不透明度之后的颜色复制对象
	 *
	 * 不透明度 = 当前不透明度 * opacity
	 *
	 * @param opacity [Float] Range(0 ~ 255)
	 * @return [Color] 原始对象
	 */
	fun opacity(opacity: Int): Color = this.copy().apply { alpha *= opacity.fixValue(checkedRange, "Opacity") }

	val hsb: HSBColor by lazy { HSBColor(this) }

	override val serialization: JsonElement
		get() = JsonPrimitive(hexStr)

	override fun deserialize(serializedObject: JsonElement) {
		serializedObject.asJsonPrimitive.apply {
			if (this.isString)
				argb = decode(this.asString)
			else if (this.isNumber)
				argb = this.asInt
		}
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Color

		if (argb != other.argb) return false

		return true
	}

	override fun hashCode(): Int {
		return argb
	}

	override fun toString(): String {
		return "Color(argb=$argb, hexStr='$hexStr', red=$red, green=$green, blue=$blue, alpha=$alpha)"
	}


}