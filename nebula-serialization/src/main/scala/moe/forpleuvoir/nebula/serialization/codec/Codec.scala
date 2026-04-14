package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.codec.{ProductCodec, ScalaEnumCodec}
import moe.forpleuvoir.nebula.serialization.extension.{SerArrayOps, SerObjectOps}

import scala.compiletime.{constValueTuple, erasedValue, summonInline}
import scala.deriving.Mirror
import scala.reflect.ClassTag
import scala.util.Try

trait Codec[T] extends Serializer[T], Deserializer[T]

object Codec {

  inline given derived[T](using m: Mirror.Of[T]): Codec[T] = {
    inline m match {
      case s: Mirror.SumOf[T] =>
        inline erasedValue[T] match {
          case e: scala.reflect.Enum =>
            enumCodec[T & scala.reflect.Enum](using s.asInstanceOf[Mirror.SumOf[T & scala.reflect.Enum]])
              .asInstanceOf[Codec[T]]
          case _ => throw new IllegalStateException("Not a Scala Enum")
        }
      case p: Mirror.ProductOf[T] => productCodec[T](using p)
    }
  }


  def create[T]: CodecBuilder[T, EmptyTuple] = CodecBuilder[T]

  def delegated[A, B](toDelegate: A => B, fromDelegate: B => A)(using Codec[B]): Codec[A] = new Codec[A] {

    override def deserialization(data: SerializeElement): Try[A] = Try {
      fromDelegate(data.deserialization[B].get)
    }

    override def serialization(value: A): SerializeElement =
      toDelegate(value).serialization

  }

  inline def decode[T](data: SerializeElement)(using deserializer: Deserializer[T]): Try[T] =
    deserializer.deserialization(data)

  inline def encode[T](data: T)(using serializer: Serializer[T]): SerializeElement = serializer.serialization(data)

  export SerArrayOps._
  export SerObjectOps._
  export PrimitiveCodec._
  export moe.forpleuvoir.nebula.serialization.extension.{deserialization, serialization}


}

private inline def enumCodec[E <: scala.reflect.Enum](using m: Mirror.SumOf[E]): Codec[E] = {
  val clazz = scala.compiletime.summonInline[ClassTag[E]].runtimeClass
  val values = clazz.getMethod("values").invoke(null).asInstanceOf[Array[E]]
  ScalaEnumCodec[E](values)
}

private inline def summonChildCodecs[T <: Tuple]: List[Codec[?]] = {
  inline erasedValue[T] match {
    case _: EmptyTuple => Nil
    case _: (t *: ts) =>
      summonInline[Codec[t]] :: summonChildCodecs[ts]
  }
}

private inline def productCodec[P](using m: Mirror.ProductOf[P]): Codec[P] = {
  val labels = constValueTuple[m.MirroredElemLabels].toList.asInstanceOf[List[String]]
  val childCodecs = summonChildCodecs[m.MirroredElemTypes]
  inline erasedValue[P] match {
    case _: Tuple =>
      ProductCodec[P](labels, childCodecs, true, m)
    case _ =>
      ProductCodec[P](labels, childCodecs, false, m)
  }
}
