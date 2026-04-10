package moe.forpleuvoir.nebula.common.api

trait Defaultable {

  def isDefault: Boolean

  def restToDefault(): this.type

}
