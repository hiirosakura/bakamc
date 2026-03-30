package moe.forpleuvoir.nebula.common.util.primitive

object CoerceInExtension {
  extension (self: Double) {

    def coerceIn(min: Double, max: Double): Double = Math.clamp(self, min, max)

    infix def in(min: Double, max: Double): Boolean = min <= self && self <= max

    infix def notIn(min: Double, max: Double): Boolean = self in(min, max)

  }


  extension (self: Float) {

    def coerceIn(min: Float, max: Float): Float = Math.clamp(self, min, max)

    infix def in(min: Float, max: Float): Boolean = min <= self && self <= max

    infix def notIn(min: Float, max: Float): Boolean = self in(min, max)

  }

  extension (self: Int) {

    def coerceIn(min: Int, max: Int): Int = Math.clamp(self, min, max)

    infix def in(min: Int, max: Int): Boolean = min <= self && self <= max

    infix def notIn(min: Int, max: Int): Boolean = self in(min, max)

  }

  extension (self: Long) {

    def coerceIn(min: Long, max: Long): Long = Math.clamp(self, min, max)

    infix def in(min: Long, max: Long): Boolean = min <= self && self <= max

    infix def notIn(min: Long, max: Long): Boolean = self in(min, max)

  }
}
