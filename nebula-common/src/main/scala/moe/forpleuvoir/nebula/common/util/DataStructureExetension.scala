package moe.forpleuvoir.nebula.common.util

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks

extension [T](self: T) {

  def countParents(parentSupplier: T => Option[T]): Int = {
    var count = 0
    var current: Option[T] = Some(self)
    Breaks.breakable {
      while (current.isDefined) {
        val parent = parentSupplier(current.get)
        if (parent.isEmpty) {
          Breaks.break()
        } else {
          count += 1
          current = parent
        }
      }
    }
    count
  }

  def pathToRoot(limit: Int = Int.MaxValue, parentSupplier: T => Option[T]): List[T] = {
    val path = ArrayBuffer[T]()
    var currentNode: Option[T] = Some(self)
    while (currentNode.isDefined && path.size <= limit) {
      path += currentNode.get
      currentNode = parentSupplier(currentNode.get)
    }
    path.reverse.toList
  }

}