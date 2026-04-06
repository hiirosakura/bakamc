package moe.forpleuvoir.nebula.serialization.json

import moe.forpleuvoir.nebula.serialization.ast.SyntaxEncoder
import moe.forpleuvoir.nebula.serialization.base.*

import scala.collection.mutable

class JsonEncoder(
  private val useIndent: Boolean = true,
  private val indentSize: Int = 2
) extends SyntaxEncoder {

  override def encode(element: SerializeElement): String = {
    val sb = new mutable.StringBuilder()
    buildString(element, sb, 0)
    sb.toString()
  }

  private def buildString(element: SerializeElement, sb: mutable.StringBuilder, depth: Int): Unit = {
    element match {
      case obj: SerializeObject =>
        encodeObject(obj, sb, depth)
      case arr: SerializeArray =>
        encodeArray(arr, sb, depth)
      case primitive: SerializePrimitive =>
        encodePrimitive(primitive, sb)
      case SerializeNull =>
        sb.append("null")
    }
  }

  private def encodeObject(obj: SerializeObject, sb: mutable.StringBuilder, depth: Int): Unit = {
    sb.append("{")
    val members = obj
    if (members.isEmpty) {
      sb.append("}")
    } else {
      appendNewLine(sb, depth + 1)
      var first = true
      members.foreach { (key, value) =>
        if (!first) {
          sb.append(",")
          appendNewLine(sb, depth + 1)
        }
        sb.append("\"").append(key).append("\":")
        if (useIndent) sb.append(" ")
        buildString(value, sb, depth + 1)
        first = false
      }
      appendNewLine(sb, depth)
      sb.append("}")
    }
  }

  private def encodeArray(arr: SerializeArray, sb: mutable.StringBuilder, depth: Int): Unit = {
    sb.append("[")
    val elements = arr
    if (elements.isEmpty) {
      sb.append("]")
    } else {
      appendNewLine(sb, depth + 1)
      var first = true
      elements.foreach { element =>
        if (!first) {
          sb.append(",")
          appendNewLine(sb, depth + 1)
        }
        buildString(element, sb, depth + 1)
        first = false
      }
      appendNewLine(sb, depth)
      sb.append("]")
    }
  }

  private def encodePrimitive(p: SerializePrimitive, sb: mutable.StringBuilder): Unit = {
    p.value match {
      case s: String =>
        sb.append("\"").append(escape(s)).append("\"")
      case other =>
        sb.append(other.toString)
    }
  }

  private def appendNewLine(sb: mutable.StringBuilder, depth: Int): Unit = {
    if (useIndent) {
      sb.append("\n")
      sb.append(" " * (depth * indentSize))
    }
  }

  private def escape(s: String): String = {
    s.flatMap {
      case '"' => "\\\""
      case '\\' => "\\\\"
      case '\b' => "\\b"
      case '\f' => "\\f"
      case '\n' => "\\n"
      case '\r' => "\\r"
      case '\t' => "\\t"
      case c if c < ' ' => f"\\u${c.toInt}%04x"
      case c => c.toString
    }
  }
}

object JsonEncoder extends SyntaxEncoder {
  val Compress = new JsonEncoder(useIndent = false)
  private val Encoder = new JsonEncoder(useIndent = true)

  override def encode(element: SerializeElement): String = Encoder.encode(element)

  def apply(useIndent: Boolean = true, indentSize: Int = 2): JsonEncoder = new JsonEncoder(useIndent, indentSize)

}