//noinspection DuplicatedCode
package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.Codec
import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializePrimitive}

//region Int
object IntCodec extends Codec[Int] {

  override def deserialize(data: SerializeElement): Int = {
    data match {
      case p: SerializePrimitive => p.asInt
      case _ => throw new IllegalArgumentException("IntCodec.deserialize: data is not a SerializePrimitive")
    }
  }

  override def serialize(value: Int): SerializeElement = SerializePrimitive.of(value)

}

given Codec[Int] = IntCodec
//endregion

//region Byte
object ByteCodec extends Codec[Byte] {

  override def deserialize(data: SerializeElement): Byte = {
    data match {
      case p: SerializePrimitive => p.asByte
      case _ => throw new IllegalArgumentException("ByteCodec.deserialize: data is not a SerializePrimitive")
    }
  }

  override def serialize(value: Byte): SerializeElement = SerializePrimitive.of(value)

}

given Codec[Byte] = ByteCodec
//endregion

//region Short
object ShortCodec extends Codec[Short] {

  override def deserialize(data: SerializeElement): Short = {
    data match {
      case p: SerializePrimitive => p.asShort
      case _ => throw new IllegalArgumentException("ShortCodec.deserialize: data is not a SerializePrimitive")
    }
  }

  override def serialize(value: Short): SerializeElement = SerializePrimitive.of(value)

}

given Codec[Short] = ShortCodec
//endregion

//region Long
object LongCodec extends Codec[Long] {

  override def deserialize(data: SerializeElement): Long = {
    data match {
      case p: SerializePrimitive => p.asLong
      case _ => throw new IllegalArgumentException("LongCodec.deserialize: data is not a SerializePrimitive")
    }
  }

  override def serialize(value: Long): SerializeElement = SerializePrimitive.of(value)

}

given Codec[Long] = LongCodec
//endregion

//region Float
object FloatCodec extends Codec[Float] {

  override def deserialize(data: SerializeElement): Float = {
    data match {
      case p: SerializePrimitive => p.asFloat
      case _ => throw new IllegalArgumentException("FloatCodec.deserialize: data is not a SerializePrimitive")
    }
  }

  override def serialize(value: Float): SerializeElement = SerializePrimitive.of(value)

}

given Codec[Float] = FloatCodec
//endregion

//region Double
object DoubleCodec extends Codec[Double] {

  override def deserialize(data: SerializeElement): Double = {
    data match {
      case p: SerializePrimitive => p.asDouble
      case _ => throw new IllegalArgumentException("DoubleCodec.deserialize: data is not a SerializePrimitive")
    }
  }

  override def serialize(value: Double): SerializeElement = SerializePrimitive.of(value)

}

given Codec[Double] = DoubleCodec
//endregion

//region String
object StringCodec extends Codec[String] {

  override def deserialize(data: SerializeElement): String = {
    data match {
      case p: SerializePrimitive => p.asString
      case _ => throw new IllegalArgumentException("StringCodec.deserialize: data is not a SerializePrimitive")
    }
  }

  override def serialize(value: String): SerializeElement = SerializePrimitive.of(value)

}

given Codec[String] = StringCodec
//endregion

//region BigDecimal
object BigDecimalCodec extends Codec[BigDecimal] {

  override def deserialize(data: SerializeElement): BigDecimal = {
    data match {
      case p: SerializePrimitive => p.asBigDecimal
      case _ => throw new IllegalArgumentException("BigDecimalCodec.deserialize: data is not a SerializePrimitive")
    }
  }

  override def serialize(value: BigDecimal): SerializeElement = SerializePrimitive.of(value)

}

given Codec[BigDecimal] = BigDecimalCodec
//endregion

//region BigInt
object BigIntCodec extends Codec[BigInt] {

  override def deserialize(data: SerializeElement): BigInt = {
    data match {
      case p: SerializePrimitive => p.asBigInteger
      case _ => throw new IllegalArgumentException("BigIntCodec.deserialize: data is not a SerializePrimitive")
    }
  }

  override def serialize(value: BigInt): SerializeElement = SerializePrimitive.of(value)

}

given Codec[BigInt] = BigIntCodec
//endregion


//region Boolean
object BooleanCodec extends Codec[Boolean] {

  override def deserialize(data: SerializeElement): Boolean = {
    data match {
      case p: SerializePrimitive => p.asBoolean
      case _ => throw new IllegalArgumentException("BooleanCodec.deserialize: data is not a SerializePrimitive")
    }
  }

  override def serialize(value: Boolean): SerializeElement = SerializePrimitive.of(value)

}

given Codec[Boolean] = BooleanCodec
//endregion