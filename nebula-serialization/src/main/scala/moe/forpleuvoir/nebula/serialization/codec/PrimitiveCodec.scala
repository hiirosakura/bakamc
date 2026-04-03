//noinspection DuplicatedCode
package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.Codec
import moe.forpleuvoir.nebula.serialization.base.{Primitive, SerializeElement, SerializePrimitive}

import scala.util.Try

object PrimitiveCodec {

  private def of[T <: Primitive](f: SerializeElement => T): Codec[T] = new Codec[T] {
    override def deserialization(data: SerializeElement): Try[T] = Try(f(data))

    override def serialization(value: T): SerializeElement = SerializePrimitive(value)
  }

  val Int: Codec[Int] = of(_.asInt.get)

  val Byte: Codec[Byte] = of(_.asByte.get)

  val Short: Codec[Short] = of(_.asShort.get)

  val Long: Codec[Long] = of(_.asLong.get)

  val Float: Codec[Float] = of(_.asFloat.get)

  val Double: Codec[Double] = of(_.asDouble.get)

  val Char: Codec[Char] = of(_.asChar.get)

  val String: Codec[String] = of(_.asString.get)

  val BigDecimal: Codec[BigDecimal] = of(_.asBigDecimal.get)

  val BigInt: Codec[BigInt] = of(_.asBigInt.get)

  val Boolean: Codec[Boolean] = of(_.asBoolean.get)

}

given Codec[Int] = PrimitiveCodec.Int

given Codec[Byte] = PrimitiveCodec.Byte

given Codec[Short] = PrimitiveCodec.Short

given Codec[Long] = PrimitiveCodec.Long

given Codec[Float] = PrimitiveCodec.Float

given Codec[Double] = PrimitiveCodec.Double

given Codec[Char] = PrimitiveCodec.Char

given Codec[String] = PrimitiveCodec.String

given Codec[BigDecimal] = PrimitiveCodec.BigDecimal

given Codec[BigInt] = PrimitiveCodec.BigInt

given Codec[Boolean] = PrimitiveCodec.Boolean