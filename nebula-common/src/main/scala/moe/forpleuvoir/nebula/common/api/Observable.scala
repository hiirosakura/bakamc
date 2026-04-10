package moe.forpleuvoir.nebula.common.api

trait Observable {

  def notifyChange(): Unit

  def observe(callback: this.type => Unit): Unit

}
