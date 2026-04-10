package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.base.{SerializeArray, SerializeElement, SerializeObject}
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.extension.SerArrayOps.*
import moe.forpleuvoir.nebula.serialization.extension.SerObjectOps.*

import scala.deriving.Mirror
import scala.util.Try

class ProductCodec[P](
  labels: List[String],
  codecs: List[Codec[?]],
  isTuple: Boolean,
  mirror: Mirror.ProductOf[P]
) extends Codec[P] {

  override def deserialization(data: SerializeElement): Try[P] = Try {
    if (isTuple) {
      decodeByTuple(data)
    } else {
      decodeByCaseClass(data)
    }
  }

  private def decodeByCaseClass(data: SerializeElement): P = {
    val obj = data.asObject.get
    val values = labels.zip(codecs).map { (label, codec) =>
      val field = obj.get(label).getOrElse {
        throw new IllegalArgumentException(s"Missing field : $label")
      }
      codec.deserialization(field).get
    }
    mirror.fromProduct(Tuple.fromArray(values.toArray).asInstanceOf[mirror.MirroredElemTypes])
  }

  private def decodeByTuple(data: SerializeElement): P = {
    val array = data.asArray.get
    val values = codecs.zipWithIndex.map { (codec, idx) =>
      val fieldValue = array(idx)
      codec.deserialization(fieldValue).get
    }
    mirror.fromProduct(Tuple.fromArray(values.toArray).asInstanceOf[mirror.MirroredElemTypes])
  }

  override def serialization(value: P): SerializeElement = {
    val p = value.asInstanceOf[Product]
    if (isTuple) {
      SerializeArray.build {
        codecs.zipWithIndex.foreach { (codec, idx) =>
          val fieldValue = p.productElement(idx)
          add(codec.asInstanceOf[Codec[Any]].serialization(fieldValue))
        }
      }
    } else {
      SerializeObject.build {
        labels.zip(codecs).zipWithIndex.foreach { case ((label, codec), idx) =>
          val fieldValue = p.productElement(idx)
          label := codec.asInstanceOf[Codec[Any]].serialization(fieldValue)
        }
      }
    }
  }
}
