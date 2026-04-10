package moe.forpleuvoir.nebula.common.util

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

extension [T](self: T) {

  inline def applySelf(inline f: T ?=> Unit): T = {
    f(using self)
    self
  }

}

extension [A <: AutoCloseable](resource: A) {

  def use[T](block: A => T): T = {
    try block(resource)
    finally resource.close()
  }

}

inline def measureTime[T](block: => T): (T, FiniteDuration) = {
  val start = System.nanoTime()
  val result = block
  val end = System.nanoTime()
  (result, FiniteDuration(end - start, TimeUnit.NANOSECONDS))
}