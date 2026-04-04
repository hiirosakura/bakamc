package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializeNull}
import moe.forpleuvoir.nebula.serialization.extension.SerObjectOps.*
import moe.forpleuvoir.nebula.serialization.extension.buildSerObject

import scala.util.Try

class CodecBuilder[T, Fields <: Tuple](
  private val fields: List[FieldInfo[T, ?]],
  private val constructor: Fields => T
) {

  def field[A](name: String)(getter: T => A)(codec: Codec[A]): CodecBuilder[T, Tuple.Append[Fields, A]] = {
    val newConstructor = (allFields: Tuple.Append[Fields, A]) => {
      val oldFields = allFields.init.asInstanceOf[Fields]
      constructor(oldFields)
    }
    new CodecBuilder(
      fields :+ FieldInfo(name, getter, codec),
      newConstructor
    )
  }

  def fieldUsingCodec[A](name: String)(getter: T => A)(using codec: Codec[A]): CodecBuilder[T, Tuple.Append[Fields, A]] = field(name)(getter)(codec)

  def apply(constructor: Fields => T): Codec[T] = new Codec[T] {
    override def serialization(value: T): SerializeElement = {
      buildSerObject {
        fields.foreach {
          case FieldInfo(name, getter, codec) =>
            val fieldValue = getter(value)
            name := codec.serialization(fieldValue)
        }
      }
    }

    override def deserialization(data: SerializeElement): Try[T] = Try {
      val obj = data.asObject.get
      val values = fields.map {
        case FieldInfo(name, _, codec) =>
          val fieldData = obj.get(name).getOrElse(SerializeNull)
          codec.deserialization(fieldData).get
      }
      constructor(Tuple.fromArray(values.toArray).asInstanceOf[Fields])
    }
  }

}

case class FieldInfo[T, A](
  name: String,
  getter: T => A,
  codec: Codec[A]
)

object CodecBuilder {

  def apply[T]: CodecBuilder[T, EmptyTuple] = {
    new CodecBuilder(
      Nil,
      (_: EmptyTuple) => throw new IllegalStateException("No fields defined")
    )
  }


}


