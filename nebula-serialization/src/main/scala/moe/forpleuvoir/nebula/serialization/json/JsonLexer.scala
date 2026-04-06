package moe.forpleuvoir.nebula.serialization.json

import moe.forpleuvoir.nebula.serialization.ast.{Lexer, Token}

import scala.annotation.tailrec

object JsonLexer extends Lexer {

  override def tokenize(input: String): List[Token] = {
    scan(input, 0, Nil)
  }

  @tailrec
  private def scan(input: String, cursor: Int, acc: List[Token]): List[Token] = {
    if (cursor >= input.length) return acc :+ Token.EOF
    val char = input(cursor)
    char match {
      //空白
      case ' ' | '\n' | '\t' | '\r' => scan(input, cursor + 1, acc)
      //符号
      case '{' | '}' | '[' | ']' | ',' | ':' => scan(input, cursor + 1, acc :+ Token.Symbol(char.toString))
      //字符串
      case '"' =>
        val (str, nextCursor) = readString(input, cursor + 1)
        scan(input, nextCursor, acc :+ Token.Literal(str))
      case c if c.isDigit || c == '-' =>
        val (numStr, nextCursor) = readNumber(input, cursor)
        scan(input, nextCursor, acc :+ Token.Literal(parseNumber(numStr)))
      case 't' | 'f' | 'n' =>
        val (word, nextCursor) = readWord(input, cursor)
        word match {
          case "true" => scan(input, nextCursor, acc :+ Token.Literal(true))
          case "false" => scan(input, nextCursor, acc :+ Token.Literal(false))
          case "null" => scan(input, nextCursor, acc :+ Token.Literal(null))
          case _ => throw new IllegalArgumentException(s"Invalid JSON identifier: $word")
        }
      case _ => throw new IllegalArgumentException(s"Unexpected character: $char at $cursor")
    }

  }

  private def readString(input: String, start: Int): (String, Int) = {
    val sb = new StringBuilder()
    var i = start
    var escaped = false
    var done = false

    while (i < input.length && !done) {
      val c = input(i)
      if (escaped) {
        c match {
          case '"' | '\\' | '/' => sb.append(c)
          case 'b' => sb.append('\b')
          case 'f' => sb.append('\f')
          case 'n' => sb.append('\n')
          case 'r' => sb.append('\r')
          case 't' => sb.append('\t')
          case 'u' =>
            val hex = input.substring(i + 1, i + 5)
            sb.append(Integer.parseInt(hex, 16).toChar)
            i += 4
          case _ => throw new IllegalArgumentException(s"Invalid escape: \\$c")
        }
        escaped = false
      } else if (c == '\\') {
        escaped = true
      } else if (c == '"') {
        done = true
      } else {
        sb.append(c)
      }
      i += 1
    }
    if (!done) throw new IllegalArgumentException("Unclosed string literal")
    (sb.toString(), i)
  }

  private def readNumber(input: String, start: Int): (String, Int) = {
    var i = start
    while (i < input.length && "0123456789.+-eE".contains(input(i))) {
      i += 1
    }
    (input.substring(start, i), i)
  }

  private def parseNumber(numStr: String): BigDecimal | Float | Double | BigInt | Int | Long = {
    if (numStr.contains('.') || numStr.toLowerCase.contains('e')) {
      val bd = BigDecimal(numStr)
      if (bd.isDecimalFloat && bd >= BigDecimal(Float.MinValue.toDouble) && bd <= BigDecimal(Float.MaxValue.toDouble)) {
        bd.toFloat
      } else if (bd.isDecimalDouble && bd >= BigDecimal(Double.MinValue) && bd <= BigDecimal(Double.MaxValue)) {
        bd.toDouble
      } else {
        bd
      }
    } else {
      val bi = BigInt(numStr)
      if (bi.isValidInt) {
        bi.toInt
      } else if (bi.isValidLong) {
        bi.toLong
      } else {
        bi
      }
    }
  }

  private def readWord(input: String, start: Int): (String, Int) = {
    var i = start
    while (i < input.length && input(i).isLetter) {
      i += 1
    }
    (input.substring(start, i), i)
  }

}
