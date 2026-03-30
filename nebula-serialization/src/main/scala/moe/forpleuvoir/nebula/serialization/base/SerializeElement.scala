//noinspection DuplicatedCode
package moe.forpleuvoir.nebula.serialization.base

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Buffer}

//region SerializeElement
sealed trait SerializeElement {

  def copy: SerializeElement

  def deepCopy: SerializeElement

  def isPrimitive: Boolean = false

  def isArray: Boolean = false

  def isObject: Boolean = false

  def isNull: Boolean = false

  private def errorType(clazz: Class[?]): UnsupportedOperationException =
    new UnsupportedOperationException(s"Cannot convert [${this.getClass.getName}] to [${clazz.getClass.getName}].")

  def asPrimitive: SerializePrimitive = throw errorType(SerializePrimitive.getClass)

  def asArray: SerializeArray = throw errorType(SerializeArray.getClass)

  def asObject: SerializeObject = throw errorType(SerializeObject.getClass)

  def asNull: SerializeNull.type = throw errorType(SerializeNull.getClass)

}
//endregion

//region SerializePrimitive
case class SerializePrimitive private(private[serialization] val value: Any) extends SerializeElement {

  override def copy: SerializePrimitive = SerializePrimitive(value)

  override def deepCopy: SerializeElement = SerializePrimitive(value)

  override def isPrimitive: Boolean = true

  override def asPrimitive: SerializePrimitive = this

  def isString: Boolean = value.isInstanceOf[String]

  def isBoolean: Boolean = value.isInstanceOf[Boolean]

  def isNumber: Boolean = value.isInstanceOf[Number]

  def isByte: Boolean = value.isInstanceOf[Byte]

  def isShort: Boolean = value.isInstanceOf[Short]

  def isInt: Boolean = value.isInstanceOf[Int]

  def isLong: Boolean = value.isInstanceOf[Long]

  def isFloat: Boolean = value.isInstanceOf[Float]

  def isDouble: Boolean = value.isInstanceOf[Double]

  def isBigInteger: Boolean = value.isInstanceOf[BigInt]

  def isBigDecimal: Boolean = value.isInstanceOf[BigDecimal]

  def asString: String = value match {
    case s: String => s
    case _ => value.toString
  }

  def asBoolean: Boolean = value match {
    case b: Boolean => b
    case _ => value.toString.toBoolean
  }

  def asNumber: Number = value match {
    case s: String => new LazilyParsedNumber(s)
    case _ => value.asInstanceOf[Number]
  }

  def asByte: Byte = value match {
    case n: Number => n.byteValue()
    case _ => value.toString.toByte
  }

  def asShort: Short = value match {
    case n: Number => n.shortValue()
    case _ => value.toString.toShort
  }

  def asInt: Int = value match {
    case n: Number => n.intValue()
    case _ => value.toString.toInt
  }

  def asLong: Long = value match {
    case n: Number => n.longValue()
    case _ => value.toString.toLong
  }

  def asFloat: Float = value match {
    case n: Number => n.floatValue()
    case _ => value.toString.toFloat
  }

  def asDouble: Double = value match {
    case n: Number => n.doubleValue()
    case _ => value.toString.toDouble
  }

  def asBigInteger: BigInt = value match {
    case i: BigInt => i
    case n: Number => BigInt(n.longValue())
    case _ => BigInt(value.toString)
  }

  def asBigDecimal: BigDecimal = value match {
    case i: BigDecimal => i
    case n: Number => BigDecimal(n.doubleValue())
    case _ => BigDecimal(value.toString)
  }

  override def toString: String = {
    if (isString) s"\"$asString\""
    else value.toString
  }

  override def hashCode(): Int = value.hashCode()

  override def equals(obj: Any): Boolean = obj match {
    case p: SerializePrimitive => p.value == value
    case _ => false
  }

}

type Primitive = String | Boolean | Int | Long | Float | Double | Byte | Short | Char | BigInt | BigDecimal

object SerializePrimitive {

  def apply(boolean: Boolean) = new SerializePrimitive(boolean)

  def apply(string: String) = new SerializePrimitive(string)

  def apply(char: Char) = new SerializePrimitive(char.toString)

  def apply(byte: Byte) = new SerializePrimitive(byte)

  def apply(short: Short) = new SerializePrimitive(short)

  def apply(int: Int) = new SerializePrimitive(int)

  def apply(long: Long) = new SerializePrimitive(long)

  def apply(float: Float) = new SerializePrimitive(float)

  def apply(double: Double) = new SerializePrimitive(double)

  def apply(bigInteger: BigInt) = new SerializePrimitive(bigInteger)

  def apply(bigDecimal: BigDecimal) = new SerializePrimitive(bigDecimal)

  def of(value: Primitive): SerializePrimitive = value match {
    case s: String => SerializePrimitive(s)
    case b: Boolean => SerializePrimitive(b)
    case i: Int => SerializePrimitive(i)
    case l: Long => SerializePrimitive(l)
    case f: Float => SerializePrimitive(f)
    case d: Double => SerializePrimitive(d)
    case b: Byte => SerializePrimitive(b)
    case s: Short => SerializePrimitive(s)
    case c: Char => SerializePrimitive(c)
    case bi: BigInt => SerializePrimitive(bi)
    case bd: BigDecimal => SerializePrimitive(bd)
  }

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

  override def asArray: SerializeArray = this

  def add(value: Primitive): SerializeArray.this.type = {
    elements.addOne(SerializePrimitive.of(value))
    this
  }

  def addAll(array: SerializeArray): SerializeArray.this.type = {
    elements.addAll(array.elements)
    this
  }

  def addAll(values: Primitive*): Unit = {
    values.foreach(value => elements.addOne(SerializePrimitive.of(value)))
  }

}

object SerializeArray {

  def apply(elements: SerializeElement | Primitive*): SerializeArray =
    new SerializeArray(ArrayBuffer.from(elements.map {
      case v: SerializeElement => v
      case v: Primitive => SerializePrimitive.of(v)
    }))

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
      sb.append(s"$key : $value")
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

  override def asObject: SerializeObject = this

  def update(key: String, value: SerializeElement | Primitive): Unit = {
    members.update(key, value match {
      case v: SerializeElement => v
      case v: Primitive => SerializePrimitive.of(v)
    })
  }

  def containsKeys(keys: String*): Boolean = {
    keys.forall(members.contains)
  }

}

object SerializeObject {

  def apply(members: (String, SerializeElement | Primitive)*): SerializeObject = {
    new SerializeObject(mutable.LinkedHashMap.from(members.map { (key, value) =>
      val v = value match {
        case v: SerializeElement => v
        case v: Primitive => SerializePrimitive.of(v)
      }
      (key, v)
    }))
  }

}
//endregion

//region SerializeNull
case object SerializeNull extends SerializeElement {

  override def isNull: Boolean = true

  override def asNull: SerializeNull.type = this

  override def copy: SerializeElement = this

  override def deepCopy: SerializeElement = this

  override def toString: String = "null"

}
//endregion