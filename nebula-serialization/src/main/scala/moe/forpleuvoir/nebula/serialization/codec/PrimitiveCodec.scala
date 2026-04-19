//noinspection DuplicatedCode
package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.base.{Primitive, SerializeElement, SerializePrimitive}
import moe.forpleuvoir.nebula.serialization.codec.Codec

import scala.util.{Success, Try}

object PrimitiveCodec {

  private def of[T <: Primitive](f: SerializeElement => T): Codec[T] = new Codec[T] {
    override def deserialization(data: SerializeElement): Try[T] = Try(f(data))

    override def serialization(value: T): SerializeElement = SerializePrimitive(value)
  }

  private def default[T <: Primitive](default: T, f: SerializeElement => T): Codec[T] = of { data =>
    try f(data) catch case _: Throwable => default
  }

  private def defaultRange[T <: Primitive](default: T, range: (T, T), f: SerializeElement => T)(using ord: Ordering[T]): Codec[T] = {
    val (min, max) = range
    require(ord.gt(max, min), s"max($max) must be greater than min($min)")
    of { data =>
      val value = try f(data) catch case _: Throwable => default
      ord.min(max, ord.max(value, min))
    }
  }

  //region Int
  val Int: Codec[Int] = of(_.asInt.get)

  def Int(defaultValue: Int): Codec[Int] = default(defaultValue, { e => e.asInt.get })

  def Int(defaultValue: Int, range: (Int, Int)): Codec[Int] = defaultRange(defaultValue, range, { e => e.asInt.get })

  def Int(range: (Int, Int)): Codec[Int] = defaultRange(range._1, range, { e => e.asInt.get })
  //endregion

  //region Byte
  val Byte: Codec[Byte] = of(_.asByte.get)

  def Byte(defaultValue: Byte): Codec[Byte] = default(defaultValue, { e => e.asByte.get })

  def Byte(defaultValue: Byte, range: (Byte, Byte)): Codec[Byte] = defaultRange(defaultValue, range, { e => e.asByte.get })

  def Byte(range: (Byte, Byte)): Codec[Byte] = defaultRange(range._1, range, { e => e.asByte.get })
  //endregion

  //region Short
  val Short: Codec[Short] = of(_.asShort.get)

  def Short(defaultValue: Short): Codec[Short] = default(defaultValue, { e => e.asShort.get })

  def Short(defaultValue: Short, range: (Short, Short)): Codec[Short] = defaultRange(defaultValue, range, { e => e.asShort.get })

  def Short(range: (Short, Short)): Codec[Short] = defaultRange(range._1, range, { e => e.asShort.get })
  //endregion

  //region Long
  val Long: Codec[Long] = of(_.asLong.get)

  def Long(defaultValue: Long): Codec[Long] = default(defaultValue, { e => e.asLong.get })

  def Long(defaultValue: Long, range: (Long, Long)): Codec[Long] = defaultRange(defaultValue, range, { e => e.asLong.get })

  def Long(range: (Long, Long)): Codec[Long] = defaultRange(range._1, range, { e => e.asLong.get })
  //endregion

  //region Float
  val Float: Codec[Float] = of(_.asFloat.get)

  def Float(defaultValue: Float): Codec[Float] = default(defaultValue, { e => e.asFloat.get })

  def Float(defaultValue: Float, range: (Float, Float)): Codec[Float] = defaultRange(defaultValue, range, { e => e.asFloat.get })

  def Float(range: (Float, Float)): Codec[Float] = defaultRange(range._1, range, { e => e.asFloat.get })
  //endregion

  //region Double
  val Double: Codec[Double] = of(_.asDouble.get)

  def Double(defaultValue: Double): Codec[Double] = default(defaultValue, { e => e.asDouble.get })

  def Double(defaultValue: Double, range: (Double, Double)): Codec[Double] = defaultRange(defaultValue, range, { e => e.asDouble.get })

  def Double(range: (Double, Double)): Codec[Double] = defaultRange(range._1, range, { e => e.asDouble.get })
  //endregion

  //region Char
  val Char: Codec[Char] = of(_.asChar.get)

  def Char(defaultValue: Char): Codec[Char] = default(defaultValue, { e => e.asChar.get })

  def Char(defaultValue: Char, range: (Char, Char)): Codec[Char] = defaultRange(defaultValue, range, { e => e.asChar.get })

  def Char(range: (Char, Char)): Codec[Char] = defaultRange(range._1, range, { e => e.asChar.get })
  //endregion

  //region String
  val String: Codec[String] = of(_.asString.get)

  def String(defaultValue: String): Codec[String] = default(defaultValue, { e => e.asString.get })
  //endregion

  //region BigDecimal
  val BigDecimal: Codec[BigDecimal] = of(_.asBigDecimal.get)

  def BigDecimal(defaultValue: BigDecimal): Codec[BigDecimal] = default(defaultValue, { e => e.asBigDecimal.get })

  def BigDecimal(defaultValue: BigDecimal, range: (BigDecimal, BigDecimal)): Codec[BigDecimal] =
    defaultRange(defaultValue, range, { e => e.asBigDecimal.get })

  def BigDecimal(range: (BigDecimal, BigDecimal)): Codec[BigDecimal] =
    defaultRange(range._1, range, { e => e.asBigDecimal.get })

  //endregion

  //region BigInt
  val BigInt: Codec[BigInt] = of(_.asBigInt.get)

  def BigInt(defaultValue: BigInt): Codec[BigInt] = default(defaultValue, { e => e.asBigInt.get })

  def BigInt(defaultValue: BigInt, range: (BigInt, BigInt)): Codec[BigInt] =
    defaultRange(defaultValue, range, { e => e.asBigInt.get })

  def BigInt(range: (BigInt, BigInt)): Codec[BigInt] =
    defaultRange(range._1, range, { e => e.asBigInt.get })
  //endregion

  //region Boolean
  val Boolean: Codec[Boolean] = of(_.asBoolean.get)

  def Boolean(defaultValue: Boolean): Codec[Boolean] = default(defaultValue, { e => e.asBoolean.get })
  //endregion

}


private class PrimitiveDefaultCodec[T <: Primitive](default: T, private val codec: Codec[T]) extends Codec[T] {

  override def deserialization(data: SerializeElement): Try[T] = Success(codec.deserialization(data).getOrElse(default))

  override def serialization(value: T): SerializeElement = codec.serialization(value)
}
