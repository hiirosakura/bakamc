package moe.forpleuvoir.nebula.serialization.hjson

import moe.forpleuvoir.nebula.serialization.ast.SyntaxEncoder
import moe.forpleuvoir.nebula.serialization.base.*
import moe.forpleuvoir.nebula.serialization.json.JsonLexer

import scala.util.Try

trait HJsonEncoder extends SyntaxEncoder {

  /**
   * 根对象是否需要带大括号
   * @return
   */
  def rootObjectQuote: Boolean = false

  override def encode(element: SerializeElement): String = {
    // 官方 HJson 倾向于 Root Object 不带大括号，这里提供标准输出
    element match {
      case obj: SerializeObject if (obj.nonEmpty && !rootObjectQuote) => encodeRootObject(obj)
      case _ => encodeWithIndent(element, 0)
    }
  }

  // 专门用于生成不带最外层大括号的 Root Object
  protected def encodeRootObject(obj: SerializeObject): String = {
    val sb = new StringBuilder
    val keys = obj.keys.iterator
    while (keys.hasNext) {
      val key = keys.next()
      val value = obj.get(key).get
      sb.append(encodeEntry(key, value, 0, ""))
      if (keys.hasNext) sb.append("\n")
    }
    sb.toString()
  }

  protected def encodeWithIndent(element: SerializeElement, indent: Int, forceQuoteString: Boolean = false): String = {
    element match {
      case obj: SerializeObject => encodeObject(obj, indent)
      case arr: SerializeArray => encodeArray(arr, indent)
      case prim: SerializePrimitive => encodePrimitive(prim.value, indent, forceQuoteString)
      case SerializeNull => "null"
    }
  }

  protected def encodeObject(obj: SerializeObject, indent: Int): String = {
    if (obj.isEmpty) return "{}"
    val sb = new StringBuilder("{\n")
    val nextIndent = indent + 2
    val spacing = " " * nextIndent

    val keys = obj.keys.iterator
    while (keys.hasNext) {
      val key = keys.next()
      val value = obj.get(key).get

      sb.append(encodeEntry(key, value, nextIndent, spacing))
        .append("\n")
    }
    sb.append(" " * indent).append("}").toString()
  }

  protected def encodeEntry(key: String, value: SerializeElement, indent: Int, spacing: String): String = {
    val formattedKey = if (shouldQuote(key)) s"\"${escapeString(key)}\"" else key
    s"$spacing$formattedKey: ${encodeWithIndent(value, indent)}"
  }

  protected def encodeArray(arr: SerializeArray, indent: Int): String = {
    if (arr.isEmpty) return "[]"

    val singleLine = arr.iterator.map(encodeWithIndent(_, 0, true)).mkString(", ")
    if (singleLine.length <= 40) return s"[$singleLine]"

    val sb = new StringBuilder("[\n")
    val nextIndent = indent + 2
    val spacing = " " * nextIndent

    val elements = arr.iterator
    while (elements.hasNext) {
      sb.append(spacing)
        .append(encodeWithIndent(elements.next(), nextIndent))
        .append("\n")
    }
    sb.append(" " * indent).append("]").toString()
  }

  protected def encodePrimitive(value: Primitive, indent: Int, forceQuoteString: Boolean): String = {
    value match {
      case s: String if s.contains("\n") =>
        val spacing = " " * indent
        // 或者保持原样但明确界限。
        val indentedContent = s.linesIterator.map(line => s"  $spacing$line").mkString("\n")
        s"'''\n$indentedContent\n$spacing'''"

      case s: String =>
        if (forceQuoteString || shouldQuote(s) || isReservedKeyword(s)) s"\"${escapeString(s)}\"" else s

      case other => other.toString
    }
  }

  protected def shouldQuote(s: String): Boolean = {
    if (s.isEmpty) return true
    val reserved = "{}[]:,#\"'"

    // 1. 包含 HJson 结构字符或空格，必须加引号
    if (s.exists(c => reserved.contains(c) || c.isWhitespace)) return true

    // 2. 检查首字符
    val first = s.head
    if (first == '-' || first == '+' || first.isDigit) {
      // 只有当它能被解析为合法数字时，作为字符串才需要加引号来区分
      // 如果解析数字失败（比如 1.0.0），其实可以不加引号
      return Try(JsonLexer.parseNumber(s)).isSuccess
    }

    // 3. 首尾不能有空格（s.trim != s 已经在 exists(isWhitespace) 里覆盖了）
    false
  }

  protected def isReservedKeyword(s: String): Boolean = {
    s == "true" || s == "false" || s == "null"
  }

  // 基础的转义逻辑，用于处理带引号的字符串内部
  protected def escapeString(s: String): String = {
    s.flatMap {
      case '"' => "\\\""
      case '\\' => "\\\\"
      case '\b' => "\\b"
      case '\f' => "\\f"
      case '\n' => "\\n"
      case '\r' => "\\r"
      case '\t' => "\\t"
      case c if c.isControl => f"\\u${c.toInt}%04x"
      case c => c.toString
    }
  }
}

object HJsonEncoder extends HJsonEncoder {


}