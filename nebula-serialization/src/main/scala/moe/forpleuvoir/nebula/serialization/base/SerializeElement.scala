//noinspection DuplicatedCode
package moe.forpleuvoir.nebula.serialization.base

import moe.forpleuvoir.nebula.serialization.codec.{Serializable, Serializer}
import moe.forpleuvoir.nebula.serialization.extension.{SerArrayOps, SerObjectOps, buildSerArray, buildSerObject}

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Buffer}
import scala.util.Try

//region SerializeElement
sealed trait SerializeElement {

  def copy: SerializeElement

  def deepCopy: SerializeElement

  def isPrimitive: Boolean = false

  def isArray: Boolean = false

  def isObject: Boolean = false

  def isNull: Boolean = false

  def asPrimitive: Option[SerializePrimitive] = Option.empty

  def asArray: Option[SerializeArray] = Option.empty

  def asObject: Option[SerializeObject] = Option.empty

  def asNull: Option[SerializeNull.type] = Option.empty

  def asChar: Option[Char] = Option.empty

  def asString: Option[String] = Option.empty

  def asBoolean: Option[Boolean] = Option.empty

  def asNumber: Option[Number] = Option.empty

  def asByte: Option[Byte] = Option.empty

  def asShort: Option[Short] = Option.empty

  def asInt: Option[Int] = Option.empty

  def asLong: Option[Long] = Option.empty

  def asFloat: Option[Float] = Option.empty

  def asDouble: Option[Double] = Option.empty

  def asBigInt: Option[BigInt] = Option.empty

  def asBigDecimal: Option[BigDecimal] = Option.empty

}
//endregion

//region SerializePrimitive
type Primitive = String | Boolean | Int | Long | Float | Double | Byte | Short | Char | BigInt | BigDecimal

case class SerializePrimitive private(private[serialization] val value: Primitive) extends SerializeElement {

  override def copy: SerializePrimitive = SerializePrimitive(value)

  override def deepCopy: SerializeElement = SerializePrimitive(value)

  override def isPrimitive: Boolean = true

  override def asPrimitive: Option[SerializePrimitive] = Option(this)

  def isChar: Boolean = value.isInstanceOf[Char]

  def isString: Boolean = value.isInstanceOf[String]

  def isBoolean: Boolean = value.isInstanceOf[Boolean]

  def isNumber: Boolean = value.isInstanceOf[Number]

  def isByte: Boolean = value.isInstanceOf[Byte]

  def isShort: Boolean = value.isInstanceOf[Short]

  def isInt: Boolean = value.isInstanceOf[Int]

  def isLong: Boolean = value.isInstanceOf[Long]

  def isFloat: Boolean = value.isInstanceOf[Float]

  def isDouble: Boolean = value.isInstanceOf[Double]

  def isBigInt: Boolean = value.isInstanceOf[BigInt]

  def isBigDecimal: Boolean = value.isInstanceOf[BigDecimal]

  override def asChar: Option[Char] = value match {
    case c: Char => Some(c)
    case s: String => if (s.nonEmpty) Some(s.charAt(0)) else None
    case _ => val str = value.toString
      if (str.nonEmpty) Some(str.charAt(0)) else None
  }

  override def asString: Option[String] = value match {
    case s: String => Some(s)
    case _ => Some(value.toString)
  }

  override def asBoolean: Option[Boolean] = value match {
    case b: Boolean => Some(b)
    case _ =>
      try Some(value.toString.toBoolean)
      catch {
        case _: Exception => None
      }
  }

  override def asNumber: Option[Number] = value match {
    case n: Number => Some(n)
    case s: String => Some(new LazilyParsedNumber(s))
    case _ => None
  }

  override def asByte: Option[Byte] = value match {
    case n: Number => Some(n.byteValue())
    case _ => Some(value.toString.toByte)
  }

  override def asShort: Option[Short] = value match {
    case n: Number => Some(n.shortValue())
    case _ => Some(value.toString.toShort)
  }

  override def asInt: Option[Int] = value match {
    case n: Number => Some(n.intValue())
    case _ => Some(value.toString.toInt)
  }

  override def asLong: Option[Long] = value match {
    case n: Number => Some(n.longValue())
    case _ => Some(value.toString.toLong)
  }

  override def asFloat: Option[Float] = value match {
    case n: Number => Some(n.floatValue())
    case _ => Some(value.toString.toFloat)
  }

  override def asDouble: Option[Double] = value match {
    case n: Number => Some(n.doubleValue())
    case _ => Some(value.toString.toDouble)
  }

  override def asBigInt: Option[BigInt] = value match {
    case i: BigInt => Some(i)
    case n: Number => Some(BigInt(n.longValue()))
    case _ => Some(BigInt(value.toString))
  }

  override def asBigDecimal: Option[BigDecimal] = value match {
    case i: BigDecimal => Some(i)
    case n: Number => Some(BigDecimal(n.doubleValue()))
    case _ => Some(BigDecimal(value.toString))
  }

  override def toString: String = value match {
    case s: String => s"\"${escape(s)}\""
    case _ => value.toString
  }

  private def escape(s: String): String = {
    val sb = new StringBuilder
    s.foreach {
      case '"' => sb.append("\\\"")
      case '\\' => sb.append("\\\\")
      case '\b' => sb.append("\\b")
      case '\f' => sb.append("\\f")
      case '\n' => sb.append("\\n")
      case '\r' => sb.append("\\r")
      case '\t' => sb.append("\\t")
      case c if c.isControl =>
        // 处理控制字符，转为 \u00XX 格式
        sb.append(f"\\u${c.toInt}%04x")
      case c => sb.append(c)
    }
    sb.toString()
  }

  override def hashCode(): Int = value.hashCode()

  override def equals(obj: Any): Boolean = obj match {
    case p: SerializePrimitive => p.value == value
    case _ => false
  }

}

object SerializePrimitive {

  def apply(value: Primitive) = new SerializePrimitive(value)

}
//endregion

//region SerializeArray
case class SerializeArray(private val elements: mutable.Buffer[SerializeElement]) extends SerializeElement, mutable.Buffer[SerializeElement] {

  //region mutable.Buffer
  override def iterator: Iterator[SerializeElement] = elements.iterator

  override def patchInPlace(from: Int, patch: IterableOnce[SerializeElement], replaced: Int): SerializeArray.this.type = {
    elements.patchInPlace(from, patch, replaced)
    this
  }

  override def prepend(elem: SerializeElement): SerializeArray.this.type = {
    elements.prepend(elem)
    this
  }

  override def length: Int = elements.length

  override def apply(idx: Int): SerializeElement = elements(idx)

  def get(idx: Int): Option[SerializeElement] = Try(elements(idx)).toOption

  override def update(idx: Int, elem: SerializeElement): Unit = elements.update(idx, elem)

  override def insert(idx: Int, elem: SerializeElement): Unit = elements.insert(idx, elem)

  override def insertAll(idx: Int, elems: IterableOnce[SerializeElement]): Unit = elements.insertAll(idx, elems)

  override def addOne(elem: SerializeElement): SerializeArray.this.type = {
    elements.addOne(elem)
    this
  }

  override def clear(): Unit = elements.clear()

  override def addAll(elems: IterableOnce[SerializeElement]): SerializeArray.this.type = {
    elements.addAll(elems)
    this
  }

  override def knownSize: Int = elements.knownSize

  override def remove(idx: Int): SerializeElement = elements.remove(idx)

  override def remove(idx: Int, count: Int): Unit = elements.remove(idx, count)
  //endregion

  override def hashCode(): Int = elements.hashCode()

  override def equals(o: Any): Boolean = o match {
    case o: SerializeArray => elements == o.elements
    case _ => false
  }

  override def copy: SerializeElement = {
    if (elements.nonEmpty) {
      SerializeArray(elements)
    } else {
      SerializeArray()
    }
  }

  override def deepCopy: SerializeElement = {
    if (elements.nonEmpty) {
      SerializeArray(elements.map(_.deepCopy))
    } else {
      SerializeArray()
    }
  }

  override def toString: String = elements.mkString("[", ", ", "]")

  override def isArray: Boolean = true

  override def asArray: Option[SerializeArray] = Option(this)

  def add(value: Primitive): SerializeArray.this.type = {
    elements.addOne(SerializePrimitive(value))
    this
  }

  def addAll(array: SerializeArray): SerializeArray.this.type = {
    elements.addAll(array.elements)
    this
  }

  def addAll(values: Primitive*): Unit = {
    values.foreach(value => elements.addOne(SerializePrimitive(value)))
  }

}

object SerializeArray {

  def apply(elements: SerializeElement | Serializable | Primitive*): SerializeArray =
    new SerializeArray(ArrayBuffer.from(elements.map {
      case v: SerializeElement => v
      case v: Primitive => SerializePrimitive(v)
      case v: Serializable => v.serialization
    }))

  def create[T](elements: T*)(using s: Serializer[T]): SerializeArray = {
    val array = SerializeArray()
    elements.foreach { elem =>
      array.addOne(s.serialization(elem))
    }
    array
  }

  def build(block: SerializeArray ?=> Unit): SerializeArray = buildSerArray(block)

  export SerArrayOps._

}
//endregion

//region SerializeObject
case class SerializeObject private(private val members: mutable.Map[String, SerializeElement]) extends SerializeElement, mutable.Map[String, SerializeElement] {

  //region mutable.Map
  override def iterator: Iterator[(String, SerializeElement)] = members.iterator

  override def knownSize: Int = members.knownSize

  override def get(key: String): Option[SerializeElement] = members.get(key)

  override def update(key: String, value: SerializeElement): Unit = members.update(key, value)

  override def addOne(elem: (String, SerializeElement)): SerializeObject.this.type = {
    members.addOne(elem)
    this
  }

  override def addAll(elems: IterableOnce[(String, SerializeElement)]): SerializeObject.this.type = {
    members.addAll(elems)
    this
  }

  override def subtractOne(elem: String): SerializeObject.this.type = {
    members.subtractOne(elem)
    this
  }

  override def subtractAll(xs: IterableOnce[String]): SerializeObject.this.type = {
    members.subtractAll(xs)
    this
  }

  override def clear(): Unit = members.clear()

  override def keySet: collection.Set[String] = members.keySet
  //endregion

  override def toString(): String = {
    val sb = StringBuffer("{")
    members.zipWithIndex.foreach { case ((key, value), index) =>
      if (index > 0) sb.append(", ")
      sb.append(s"$key: $value")
    }
    sb.append("}").toString
  }

  override def hashCode(): Int = members.hashCode()

  override def equals(o: Any): Boolean = o match {
    case o: SerializeObject => members == o.members
    case _ => false
  }

  override def copy: SerializeElement = {
    if (members.nonEmpty) {
      SerializeObject(members)
    } else {
      SerializeObject()
    }
  }

  override def deepCopy: SerializeElement = {
    if (members.nonEmpty) {
      val serializeObject = SerializeObject()
      serializeObject.addAll(members.view.mapValues(_.deepCopy))
    } else {
      SerializeObject()
    }
  }

  override def isObject: Boolean = true

  override def asObject: Option[SerializeObject] = Option(this)

  def update(key: String, value: SerializeElement | Serializable | Primitive): Unit = {
    members.update(key, value match {
      case v: SerializeElement => v
      case v: Primitive => SerializePrimitive(v)
      case v: Serializable => v.serialization
    })
  }

  def containsKeys(keys: String*): Boolean = {
    keys.forall(members.contains)
  }

}

object SerializeObject {

  def apply(members: (String, SerializeElement | Serializable | Primitive)*): SerializeObject = {
    new SerializeObject(mutable.LinkedHashMap.from(members.map { (key, value) =>
      val v = value match {
        case v: SerializeElement => v
        case v: Primitive => SerializePrimitive(v)
        case v: Serializable => v.serialization
      }
      (key, v)
    }))
  }

  def apply(members: Map[String, SerializeElement | Primitive]): SerializeObject = {
    new SerializeObject(mutable.LinkedHashMap.from(members.map { (key, value) =>
      val v = value match {
        case v: SerializeElement => v
        case v: Primitive => SerializePrimitive(v)
      }
      (key, v)
    }))
  }

  def build(block: SerializeObject ?=> Unit): SerializeObject = buildSerObject(block)

  export SerObjectOps._

}
//endregion

//region SerializeNull
case object SerializeNull extends SerializeElement {

  override def isNull: Boolean = true

  override def asNull: Option[SerializeNull.type] = Option(this)

  override def copy: SerializeElement = this

  override def deepCopy: SerializeElement = this

  override def toString: String = "null"

}
//endregion