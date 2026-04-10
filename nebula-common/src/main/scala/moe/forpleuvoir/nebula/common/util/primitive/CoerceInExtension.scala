package moe.forpleuvoir.nebula.common.util.primitive

import scala.collection.immutable.NumericRange
import scala.math.BigDecimal
import scala.math.BigDecimal.double2bigDecimal

object CoerceInExtension {
  extension (self: Double) {

    def clamp(min: Double, max: Double): Double = Math.clamp(self, min, max)

    def clamp(range: Range.Partial[BigDecimal, NumericRange.Inclusive[BigDecimal]]): Double = {
      val r = range.by(0)
      Math.clamp(self, r.start.toDouble, r.end.toDouble)
    }

    def clamp(range: (Double, Double)): Double = Math.clamp(self, range._1, range._2)

    infix def in(min: Double, max: Double): Boolean = min <= self && self <= max

    infix def notIn(min: Double, max: Double): Boolean = !(self in(min, max))

  }


  extension (self: Float) {

    def clamp(min: Float, max: Float): Float = Math.clamp(self, min, max)

    def clamp(range: Range.Partial[BigDecimal, NumericRange.Inclusive[BigDecimal]]): Float = {
      val r = range.by(0)
      Math.clamp(self, r.start.toFloat, r.end.toFloat)
    }

    def clamp(range: (Float, Float)): Float = Math.clamp(self, range._1, range._2)

    infix def in(min: Float, max: Float): Boolean = min <= self && self <= max

    infix def notIn(min: Float, max: Float): Boolean = !(self in(min, max))

  }

  extension (self: Int) {

    def clamp(min: Int, max: Int): Int = Math.clamp(self, min, max)

    def clamp(range: Range): Int = Math.clamp(self, range.start, range.end)

    def clamp(range: (Int, Int)): Int = Math.clamp(self, range._1, range._2)

    infix def in(min: Int, max: Int): Boolean = min <= self && self <= max

    infix def notIn(min: Int, max: Int): Boolean = !(self in(min, max))

  }

  extension (self: Long) {

    def clamp(min: Long, max: Long): Long = Math.clamp(self, min, max)

    def clamp(range: Range.Partial[BigDecimal, NumericRange.Inclusive[BigDecimal]]): Long = {
      val r = range.by(0)
      Math.clamp(self, r.start.toLong, r.end.toLong)
    }

    def clamp(range: (Long, Long)): Long = Math.clamp(self, range._1, range._2)

    infix def in(min: Long, max: Long): Boolean = min <= self && self <= max

    infix def notIn(min: Long, max: Long): Boolean = !(self in(min, max))

  }
}
