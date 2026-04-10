package moe.forpleuvoir.nebula.config

import moe.forpleuvoir.nebula.common.api.{Defaultable, Observable}

trait ConfigValued[C] extends Defaultable, Observable {

  protected var _value: C

  def defaultValue: C

  override def isDefault: Boolean = valueEquals(defaultValue, _value)

  override def restToDefault(): this.type = {
    setValue(defaultValue)
    this
  }

  protected infix def valueEquals(a: C, b: C): Boolean = a == b

  /**
   * 核心实现,其他获取方法都应该直接或间接调用此方法
   *
   * @return C
   */
  def getValue: C = _value


  /**
   * 核心实现,其他设置方法都应该直接或间接调用此方法
   *
   * @param value C
   */
  def setValue(value: C): this.type = {
    if (!valueEquals(value, getValue)) {
      _value = value
      notifyChange()
    }
    this
  }

  def asString: String = getValue.toString

}

object ConfigValued {

  extension [C](self: ConfigValued[C]) {

    def apply(): C = self.getValue

    def value: C = self.getValue

    def update(value: C): self.type = {
      self.setValue(value)
      self
    }

    def value_=(value: C): Unit = self.setValue(value)

    infix def :=(value: C): Unit = self.setValue(value)

  }

}
