package moe.forpleuvoir.nebula.serialization.codec

import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializeNull}
import moe.forpleuvoir.nebula.serialization.extension.SerObjectOps.*
import moe.forpleuvoir.nebula.serialization.extension.buildSerObject

import scala.util.Try

// --- 基础定义保持不变 ---
private case class FieldInfo[T, A](
  name: String,
  getter: T => A,
  codec: Codec[A],
  default: Option[A]
)

class CodecBuilder[T, Fields <: Tuple](
  private val fields: List[FieldInfo[T, ?]]
) {

  // 1. 入口：开始定义字段
  def field(name: String): FieldInitial[T, Fields] = new FieldInitial(this, name)

  // 内部追加方法
  private[codec] def addField[A](info: FieldInfo[T, A]): CodecBuilder[T, Tuple.Append[Fields, A]] = {
    new CodecBuilder(fields :+ info)
  }

  def build(constructor: Fields => T): Codec[T] = new Codec[T] {
    override def serialization(value: T): SerializeElement = buildSerObject {
      fields.foreach { f =>
        val info = f.asInstanceOf[FieldInfo[T, Any]]
        info.name := info.codec.serialization(info.getter(value))
      }
    }

    override def deserialization(data: SerializeElement): Try[T] = Try {
      val obj = data.asObject.getOrElse(throw new Exception("Not an object"))
      val values = fields.map { f =>
        val info = f.asInstanceOf[FieldInfo[T, Any]]
        val fieldData = obj.get(info.name).getOrElse(SerializeNull)
        info.codec.deserialization(fieldData)
          .recover { case _ if info.default.isDefined => info.default.get }
          .getOrElse(throw new NoSuchElementException(s"Field '${info.name}' is missing and has no default"))
      }
      constructor(Tuple.fromArray(values.toArray).asInstanceOf[Fields])
    }
  }
}

// --- 状态 1: 刚调用完 .field("...") ---
class FieldInitial[T, Fields <: Tuple](builder: CodecBuilder[T, Fields], name: String) {

  // 先设置 Getter，进入状态 2
  def getter[A](g: T => A): FieldWithGetter[T, Fields, A] =
    new FieldWithGetter(builder, name, g)

  // 先设置 Codec，进入状态 3
  def codec[A](c: Codec[A]): FieldWithCodec[T, Fields, A] =
    new FieldWithCodec(builder, name, c)

  def usingCodec[A](using c: Codec[A]): FieldWithCodec[T, Fields, A] = codec(c)
}

// --- 状态 2: 已经有了 Getter，等 Codec 结束 ---
class FieldWithGetter[T, Fields <: Tuple, A](
  builder: CodecBuilder[T, Fields],
  name: String,
  _getter: T => A,
  _default: Option[A] = None
) {
  def default(d: A): FieldWithGetter[T, Fields, A] = new FieldWithGetter(builder, name, _getter, Some(d))

  // 用 Codec 结尾，直接回到 Builder
  def codec(c: Codec[A]): CodecBuilder[T, Tuple.Append[Fields, A]] =
    builder.addField(FieldInfo(name, _getter, c, _default))

  def usingCodec(using c: Codec[A]): CodecBuilder[T, Tuple.Append[Fields, A]] = codec(c)
}

// --- 状态 3: 已经有了 Codec，等 Getter 结束 ---
class FieldWithCodec[T, Fields <: Tuple, A](
  builder: CodecBuilder[T, Fields],
  name: String,
  _codec: Codec[A],
  _default: Option[A] = None
) {
  def default(d: A): FieldWithCodec[T, Fields, A] = new FieldWithCodec(builder, name, _codec, Some(d))

  // 用 Getter 结尾，直接回到 Builder
  def getter(g: T => A): CodecBuilder[T, Tuple.Append[Fields, A]] =
    builder.addField(FieldInfo(name, g, _codec, _default))
}

object CodecBuilder {
  def apply[T]: CodecBuilder[T, EmptyTuple] = new CodecBuilder(Nil)
}