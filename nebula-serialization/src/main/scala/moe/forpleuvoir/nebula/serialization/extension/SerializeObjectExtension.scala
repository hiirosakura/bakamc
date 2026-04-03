package moe.forpleuvoir.nebula.serialization.extension

import moe.forpleuvoir.nebula.serialization.Serializer
import moe.forpleuvoir.nebula.serialization.base.*

def buildSerObject(block: SerializeObject ?=> Unit): SerializeObject = {
  val obj = SerializeObject()
  block(using obj)
  obj
}

inline def serObject(members: (String, SerializeElement | Primitive)*) = SerializeObject(members *)

def serObject(members: Map[String, SerializeElement | Primitive]) = SerializeObject(members)

def serObject[T](members: (String, T)*)(using s: Serializer[T]) = SerializeObject(members.map { (k, v) => (k, s.serialization(v)) } *)

extension (key: String)(using obj: SerializeObject) {
  infix def :=(value: SerializeElement | Primitive): Unit =
    obj.update(key, value)

  infix def :=[T](value: T)(using s: Serializer[T]): Unit =
    obj.update(key, s.serialization(value))

  infix def !:=(value: Any): Unit = {
    obj.update(key, value.toSerializeElement)
  }
}

extension (obj: SerializeObject) {

  def getString(key: String): Option[String] = {
    obj.get(key) match {
      case Some(value) => value.asPrimitive match {
        case Some(v) => if (v.isString) {
          v.asString
        } else None
        case None => None
      }
      case None => None
    }
  }

  def getAsString(key: String): Option[String] = {
    obj.get(key) match {
      case Some(value) => value.asString
      case None => None
    }
  }

  def getBoolean(key: String): Option[Boolean] = {
    obj.get(key) match {
      case Some(value) => value.asPrimitive match {
        case Some(v) => if (v.isBoolean) {
          v.asBoolean
        } else None
        case None => None
      }
      case None => None
    }
  }

  def getAsBoolean(key: String): Option[Boolean] = {
    obj.get(key) match {
      case Some(value) => value.asBoolean
      case None => None
    }
  }

  def getByte(key: String): Option[Byte] = {
    obj.get(key) match {
      case Some(value) => value.asPrimitive match {
        case Some(v) => if (v.isByte) {
          v.asByte
        } else None
        case None => None
      }
      case None => None
    }
  }

  def getAsByte(key: String): Option[Byte] = {
    obj.get(key) match {
      case Some(value) => value.asByte
      case None => None
    }
  }

  def getInt(key: String): Option[Int] = {
    obj.get(key) match {
      case Some(value) => value.asPrimitive match {
        case Some(v) => if (v.isInt) {
          v.asInt
        } else None
        case None => None
      }
      case None => None
    }
  }

  def getAsInt(key: String): Option[Int] = {
    obj.get(key) match {
      case Some(value) => value.asInt
      case None => None
    }
  }

  def getLong(key: String): Option[Long] = {
    obj.get(key) match {
      case Some(value) => value.asPrimitive match {
        case Some(v) => if (v.isLong) {
          v.asLong
        } else None
        case None => None
      }
      case None => None
    }
  }

  def getAsLong(key: String): Option[Long] = {
    obj.get(key) match {
      case Some(value) => value.asLong
      case None => None
    }
  }

  def getFloat(key: String): Option[Float] = {
    obj.get(key) match {
      case Some(value) => value.asPrimitive match {
        case Some(v) => if (v.isFloat) {
          v.asFloat
        } else None
        case None => None
      }
      case None => None
    }
  }

  def getAsFloat(key: String): Option[Float] = {
    obj.get(key) match {
      case Some(value) => value.asFloat
      case None => None
    }
  }

  def getDouble(key: String): Option[Double] = {
    obj.get(key) match {
      case Some(value) => value.asPrimitive match {
        case Some(v) => if (v.isDouble) {
          v.asDouble
        } else None
        case None => None
      }
      case None => None
    }
  }

  def getAsDouble(key: String): Option[Double] = {
    obj.get(key) match {
      case Some(value) => value.asDouble
      case None => None
    }
  }

  def getShort(key: String): Option[Short] = {
    obj.get(key) match {
      case Some(value) => value.asPrimitive match {
        case Some(v) => if (v.isShort) {
          v.asShort
        } else None
        case None => None
      }
      case None => None
    }
  }

  def getAsShort(key: String): Option[Short] = {
    obj.get(key) match {
      case Some(value) => value.asShort
      case None => None
    }
  }

  def getChar(key: String): Option[Char] = {
    obj.get(key) match {
      case Some(value) => value.asPrimitive match {
        case Some(v) => if (v.isChar) {
          v.asChar
        } else None
        case None => None
      }
      case None => None
    }
  }

  def getAsChar(key: String): Option[Char] = {
    obj.get(key) match {
      case Some(value) => value.asChar
      case None => None
    }
  }

  def getBigInt(key: String): Option[BigInt] = {
    obj.get(key) match {
      case Some(value) => value.asPrimitive match {
        case Some(v) => if (v.isBigInt) {
          v.asBigInt
        } else None
        case None => None
      }
      case None => None
    }
  }

  def getAsBigInt(key: String): Option[BigInt] = {
    obj.get(key) match {
      case Some(value) => value.asBigInt
      case None => None
    }
  }

  def getBigDecimal(key: String): Option[BigDecimal] = {
    obj.get(key) match {
      case Some(value) => value.asPrimitive match {
        case Some(v) => if (v.isBigDecimal) {
          v.asBigDecimal
        } else None
        case None => None
      }
      case None => None
    }
  }

  def getAsBigDecimal(key: String): Option[BigDecimal] = {
    obj.get(key) match {
      case Some(value) => value.asBigDecimal
      case None => None
    }
  }

  def getAsArray(key: String): Option[SerializeArray] = {
    obj.get(key) match {
      case Some(value) => value.asArray
      case None => None
    }
  }

  def getAsObject(key: String): Option[SerializeObject] = {
    obj.get(key) match {
      case Some(value) => value.asObject
      case None => None
    }
  }

  def getAsNull(key: String): Option[SerializeNull.type] = {
    obj.get(key) match {
      case Some(value) => value.asNull
      case None => None
    }
  }

  def getOrElse(key: String, default: => String): String = {
    obj.getString(key).getOrElse(default)
  }

  def getOrElse(key: String, default: => Boolean): Boolean = {
    obj.getBoolean(key).getOrElse(default)
  }

  def getOrElse(key: String, default: => Byte): Byte = {
    obj.getByte(key).getOrElse(default)
  }

  def getOrElse(key: String, default: => Int): Int = {
    obj.getInt(key).getOrElse(default)
  }

  def getOrElse(key: String, default: => Long): Long = {
    obj.getLong(key).getOrElse(default)
  }

  def getOrElse(key: String, default: => Float): Float = {
    obj.getFloat(key).getOrElse(default)
  }

  def getOrElse(key: String, default: => Double): Double = {
    obj.getDouble(key).getOrElse(default)
  }

  def getOrElse(key: String, default: => Short): Short = {
    obj.getShort(key).getOrElse(default)
  }

  def getOrElse(key: String, default: => Char): Char = {
    obj.getChar(key).getOrElse(default)
  }

  def getOrElse(key: String, default: => BigInt): BigInt = {
    obj.getBigInt(key).getOrElse(default)
  }

  def getOrElse(key: String, default: => BigDecimal): BigDecimal = {
    obj.getBigDecimal(key).getOrElse(default)
  }

}
