package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializePrimitive}

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.util.Try

object DurationCodec extends Codec[Duration]{

  override def deserialization(data: SerializeElement): Try[Duration] = Try{
    data match {
      case SerializePrimitive(s:String) => Duration(s)
      case _=> throw new IllegalArgumentException("DurationCodec.deserialization: data is not a string")
    }
  }

  override def serialization(value: Duration): SerializeElement = SerializePrimitive(value.toString)

}

given Codec[Duration] = DurationCodec


object FiniteDurationCodec extends Codec[FiniteDuration] {

  override def deserialization(data: SerializeElement): Try[FiniteDuration] = Try {
    data match {
      case SerializePrimitive(s: String) => Duration(s) match {
        case fd: FiniteDuration => fd
        case _ => throw new IllegalArgumentException(s"Expected finite duration, got: $s")
      }
      case _ => throw new IllegalArgumentException("FiniteDurationCodec.deserialization: data is not a string")
    }
  }

  override def serialization(value: FiniteDuration): SerializeElement = SerializePrimitive(value.toString)

}

given Codec[FiniteDuration] = FiniteDurationCodec