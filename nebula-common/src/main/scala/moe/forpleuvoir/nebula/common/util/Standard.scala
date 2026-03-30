package moe.forpleuvoir.nebula.common.util

extension [T](self: T) {

  inline def applySelf(inline f: (s: T) ?=> Unit): T = {
    f(using self)
    self
  }

}