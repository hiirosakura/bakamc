package moe.forpleuvoir.nebula.common.util

import moe.forpleuvoir.nebula.common.util.Lerp.check

trait Lerp[T] {

  def lerp(to: T, fraction: Float): T

}

object Lerp {

  def check[T](fraction: T)(using n: Numeric[T]): Unit = require(n.gteq(fraction, n.zero) && n.lteq(fraction, n.one), "fraction must be between 0.0 and 1.0")

}

extension (self: Int) {
  def lerp(to: Int, fraction: Float): Int = {
    check(fraction)
    self + fraction * (to - self)
  }.toInt
}

extension (self: Long) {
  def lerp(to: Long, fraction: Float): Long = {
    check(fraction)
    self + fraction * (to - self)
  }.toLong
}

extension (self: Float) {
  def lerp(to: Float, fraction: Float): Float = {
    check(fraction)
    self + fraction * (to - self)
  }
}

extension (self: Double) {
  def lerp(to: Double, fraction: Float): Double = {
    check(fraction)
    self + fraction * (to - self)
  }
}