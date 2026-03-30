package moe.forpleuvoir.nebula.serialization.base

private[base] class LazilyParsedNumber(private val value: String) extends Number {

  override def intValue(): Int = try {
    value.toInt
  } catch {
    case _: NumberFormatException =>
      try {
        value.toLong.toInt
      } catch {
        case _: NumberFormatException =>
          BigDecimal(value).intValue
      }
  }

  override def longValue(): Long = try {
    value.toLong
  } catch {
    case _: NumberFormatException =>
      BigDecimal(value).longValue
  }

  override def floatValue(): Float = value.toFloat

  override def doubleValue(): Double = value.toDouble

  override def toString: String = value

  override def equals(obj: Any): Boolean = {
    if (obj == this) {
      return true
    }
    obj match {
      case other: LazilyParsedNumber => value == other.value || value.equals(other.value)
      case _ => false
    }
  }
}
