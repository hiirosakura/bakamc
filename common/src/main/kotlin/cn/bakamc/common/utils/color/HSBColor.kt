package cn.bakamc.common.utils.color

import cn.bakamc.common.utils.clamp
import cn.bakamc.common.utils.color.Color.Companion.fixValue
import cn.bakamc.common.utils.f
import cn.bakamc.common.utils.i

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.utils.color

 * 文件名 HSBColor

 * 创建时间 2022/3/14 0:00

 * @author forpleuvoir

 */
class HSBColor(val color: Color) {

	private val checkedRange: Boolean get() = color.checkedRange

	/**
	 * 色相 Range(0.0F ~ 360.0F)
	 */
	var hue: Float = 360F
		set(value) {
			field = value.fixValue(checkedRange, "Hue", maxValue = 360F)
		}

	/**
	 * 饱和度 Range(0.0F ~ 100.0F)
	 */
	var saturation: Float = 100F
		set(value) {
			field = value.fixValue(checkedRange, "Saturation", maxValue = 100F)
		}

	/**
	 * 明度 Range(0.0F ~ 100.0F)
	 */
	var brightness: Float = 100F
		set(value) {
			field = value.fixValue(checkedRange, "Brightness", maxValue = 100F)
		}

	/**
	 * 不透明度 Range(0.0F ~ 1.0F)
	 */
	var alpha: Float = 100F
		set(value) {
			field = value.fixValue(checkedRange, "Alpha")
		}


	var argb: Int
		get() {
			val saturation = this.saturation / 100
			val brightness = this.brightness / 100
			val i = ((this.hue / 60) % 6).i
			val f = hue / 60 - i
			val p = brightness * (1F - saturation)
			val q = brightness * (1F - f * saturation)
			val t = brightness * (1F - (1F - f) * saturation)
			val rgb: Array<Float> = when (i) {
				0    -> arrayOf(brightness, t, p)
				1    -> arrayOf(q, brightness, p)
				2    -> arrayOf(p, brightness, t)
				3    -> arrayOf(p, q, brightness)
				4    -> arrayOf(t, p, brightness)
				5    -> arrayOf(brightness, p, q)
				else -> arrayOf(0F, 0F, 0F)
			}.apply {
				for (j in this.indices) {
					this[j] *= 255F
				}
			}
			return ((alpha * 255).i shl 24) or (rgb[0].i shl 16) or (rgb[1].i shl 8) or (rgb[2].i shl 0)
		}
		set(value) {
			val r = value shr 16 and 0xFF
			val g = value shr 8 and 0xFF
			val b = value and 0xFF
			val rgb = arrayOf(r, g, b).apply { sort() }
			val max = rgb[2]
			val min = rgb[0]
			val brightness = max.f / 255
			val saturation = if (max == 0) 0.0f else 1.0f - (min.f / max)
			val hue = if (max == min) {
				0.0f
			} else if (max == r && g >= b) {
				60f * ((g - b).f / (max - min)) + 0.0f
			} else if (max == r) {
				60f * ((g - b).f / (max - min)) + 360.0f
			} else if (max == g) {
				60f * ((b - r).f / (max - min)) + 120.0f
			} else if (max == b) {
				60f * ((r - g).f / (max - min)) + 240.0f
			} else 0.0f
			this.hue = hue.clamp(0..360)
			this.saturation = saturation * 100.clamp(0..100)
			this.brightness = brightness * 100.clamp(0..100)
			this.alpha = (value shr 24 and 0xFF).f / 255
		}

	init {
		this.argb = color.argb
	}

	/**
	 * 将此对象颜色同步到[color]
	 */
	fun syncToColor() {
		color.argb = this.argb
	}

	/**
	 * 将[color]颜色同步到此对象
	 */
	fun syncFromColor() {
		this.argb = color.argb
	}

	override fun toString(): String {
		return "HSBColor(hue=$hue, saturation=$saturation, brightness=$brightness, alpha=$alpha)"
	}


}

