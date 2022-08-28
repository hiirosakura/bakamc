package cn.bakamc.common.utils

/**
 *

 * 项目名 BakaMC

 * 包名 cn.bakamc.common.utils

 * 文件名 NumberUtil

 * 创建时间 2022/7/2 22:29

 * @author forpleuvoir

 */
fun Number.clamp(minValue: Number, maxValue: Number): Number {
	if (this.toDouble() < minValue.toDouble()) {
		return minValue
	}
	return if (this.toDouble() > maxValue.toDouble()) {
		maxValue
	} else this
}

fun min(x: Double, y: Double, z: Double): Double {
	val min: Double = if (x < y) x else y
	return if (min < z) min else z
}

fun min(x: Float, y: Float, z: Float): Float {
	val min: Float = if (x < y) x else y
	return if (min < z) min else z
}

fun min(x: Int, y: Int, z: Int): Int {
	val min: Int = if (x < y) x else y
	return if (min < z) min else z
}

fun max(x: Double, y: Double, z: Double): Double {
	val max: Double = if (x > y) x else y
	return if (max > z) max else z
}

fun max(x: Float, y: Float, z: Float): Float {
	val max: Float = if (x > y) x else y
	return if (max > z) max else z
}

fun max(x: Int, y: Int, z: Int): Int {
	val max: Int = if (x > y) x else y
	return if (max > z) max else z
}

fun Byte.clamp(minValue: Number, maxValue: Number): Byte {
	return (this as Number).clamp(minValue, maxValue).toByte()
}

fun Short.clamp(minValue: Number, maxValue: Number): Short {
	return (this as Number).clamp(minValue, maxValue).toShort()
}

fun Int.clamp(minValue: Number, maxValue: Number): Int {
	return (this as Number).clamp(minValue, maxValue).toInt()
}

fun Int.clamp(range: IntRange): Int {
	return if (range.first > this) {
		range.first
	} else if (range.last < this) {
		range.last
	} else {
		this
	}
}

fun Long.clamp(minValue: Number, maxValue: Number): Long {
	return (this as Number).clamp(minValue, maxValue).toLong()
}

fun Long.clamp(range: LongRange): Long {
	return if (range.first > this) {
		range.first
	} else if (range.last < this) {
		range.last
	} else {
		this
	}
}

fun Double.clamp(minValue: Number, maxValue: Number): Double {
	return (this as Number).clamp(minValue, maxValue).toDouble()
}

fun Double.clamp(range: IntRange): Double {
	return if (range.first > this) {
		range.first.d
	} else if (range.last < this) {
		range.last.d
	} else {
		this
	}
}

fun Float.clamp(minValue: Number, maxValue: Number): Float {
	return (this as Number).clamp(minValue, maxValue).toFloat()
}

fun Float.clamp(range: IntRange): Float {
	return if (range.first > this) {
		range.first.f
	} else if (range.last < this) {
		range.last.f
	} else {
		this
	}
}

fun <T> T.isIn(vararg args: T): Boolean {
	for (arg in args) {
		if (this == arg) return true
	}
	return false
}

inline val Number.d: Double get() = this.toDouble()

inline val Number.i: Int get() = this.toInt()

inline val Number.l: Long get() = this.toLong()

inline val Number.f: Float get() = this.toFloat()