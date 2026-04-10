package moe.forpleuvoir.nebula.common.api

trait Matchable[T] {

  def test(t: T): Boolean

}
