package moe.forpleuvoir.nebula.common.util

import scala.annotation.tailrec

extension [T](self: T) {

  def countParents(parentSupplier: T => Option[T]): Int = {
    @tailrec
    def loop(current: T, count: Int): Int = {
      parentSupplier(current) match {
        case Some(parent) => loop(parent, count + 1)
        case None => count
      }
    }
    loop(self, 0)
  }


  def parents(limit: Int = Int.MaxValue)(parentSupplier: T => Option[T]): List[T] = {
    @tailrec
    def loop(current: T, acc: List[T], depth: Int): List[T] = {
      if (depth >= limit) acc.reverse
      else parentSupplier(current) match {
        case Some(parent) => loop(parent, current :: acc, depth + 1)
        case None => (current :: acc).reverse
      }
    }
    loop(self, Nil, 0)
  }

}