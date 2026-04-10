package moe.forpleuvoir.nebula.config.item

import moe.forpleuvoir.nebula.config.ConfigItem
import moe.forpleuvoir.nebula.serialization.base.{Primitive, SerializeElement}
import moe.forpleuvoir.nebula.serialization.codec.Codec

class ConfigPrimitive[T <: Primitive](
  name: String,
  defaultValue: T,
  codec: Codec[T]
) extends ConfigItem[T](name, defaultValue) {

  override def serialization: SerializeElement = codec.serialization(this.value)

  override def deserialization(data: SerializeElement): Unit = {
    codec.deserialization(data).foreach { value => setValue(value) }
  }

}


class ConfigNumber[T <: Primitive : Ordering](
  name: String,
  defaultValue: T,
  val range: (T, T),
  codec: Codec[T]
) extends ConfigPrimitive(name, defaultValue, codec) {

  require(
    Ordering[T].lteq(range._1, range._2),
    s"min[${range._1}] must be less than or equal to max[${range._2}]"
  )

  private def clamp(v: T, min: T, max: T)(using ord: Ordering[T]): T = {
    if (ord.lt(v, min)) min
    else if (ord.gt(v, max)) max
    else v
  }

  _value = clamp(this.defaultValue, range._1, range._2)

  override def setValue(value: T): ConfigNumber.this.type = {
    super.setValue(clamp(value, range._1, range._2))
  }

}

//region Byte

class ConfigByte(
  name: String,
  defaultValue: Byte,
  range: (Byte, Byte) = (Byte.MinValue, Byte.MaxValue)
) extends ConfigNumber[Byte](name, defaultValue, range, Codec.Byte)

object ConfigByte {
  def apply(name: String, defaultValue: Byte, range: (Byte, Byte) = (Byte.MinValue, Byte.MaxValue)): ConfigByte =
    new ConfigByte(name, defaultValue, range)
}

//endregion


//region Short

class ConfigShort(
  name: String,
  defaultValue: Short,
  range: (Short, Short) = (Short.MinValue, Short.MaxValue)
) extends ConfigNumber[Short](name, defaultValue, range, Codec.Short)

object ConfigShort {
  def apply(name: String, defaultValue: Short, range: (Short, Short) = (Short.MinValue, Short.MaxValue)): ConfigShort =
    new ConfigShort(name, defaultValue, range)
}

//endregion

//region Int

class ConfigInt(
  name: String,
  defaultValue: Int,
  range: (Int, Int) = (Int.MinValue, Int.MaxValue)
) extends ConfigNumber[Int](name, defaultValue, range, Codec.Int)

object ConfigInt {
  def apply(name: String, defaultValue: Int, range: (Int, Int) = (Int.MinValue, Int.MaxValue)): ConfigInt =
    new ConfigInt(name, defaultValue, range)
}

//endregion

//region Long

class ConfigLong(
  name: String,
  defaultValue: Long,
  range: (Long, Long) = (Long.MinValue, Long.MaxValue)
) extends ConfigNumber[Long](name, defaultValue, range, Codec.Long)

object ConfigLong {
  def apply(name: String, defaultValue: Long, range: (Long, Long) = (Long.MinValue, Long.MaxValue)): ConfigLong =
    new ConfigLong(name, defaultValue, range)
}

//endregion

//region Float
class ConfigFloat(
  name: String,
  defaultValue: Float,
  range: (Float, Float) = (Float.MinValue, Float.MaxValue)
) extends ConfigNumber[Float](name, defaultValue, range, Codec.Float)

object ConfigFloat {
  def apply(name: String, defaultValue: Float, range: (Float, Float) = (Float.MinValue, Float.MaxValue)): ConfigFloat =
    new ConfigFloat(name, defaultValue, range)
}
//endregion

//region Double

class ConfigDouble(
  name: String,
  defaultValue: Double,
  range: (Double, Double) = (Double.MinValue, Double.MaxValue)
) extends ConfigNumber[Double](name, defaultValue, range, Codec.Double)

object ConfigDouble {
  def apply(name: String, defaultValue: Double, range: (Double, Double) = (Double.MinValue, Double.MaxValue)): ConfigDouble =
    new ConfigDouble(name, defaultValue, range)
}

//endregion


//region String

class ConfigString(
  name: String,
  defaultValue: String
) extends ConfigPrimitive[String](name, defaultValue, Codec.String)

object ConfigString {
  def apply(name: String, defaultValue: String): ConfigString =
    new ConfigString(name, defaultValue)
}

//endregion

//region Boolean

class ConfigBoolean(
  name: String,
  defaultValue: Boolean
) extends ConfigPrimitive[Boolean](name, defaultValue, Codec.Boolean)

object ConfigBoolean {
  def apply(name: String, defaultValue: Boolean): ConfigBoolean =
    new ConfigBoolean(name, defaultValue)
}

//endregion