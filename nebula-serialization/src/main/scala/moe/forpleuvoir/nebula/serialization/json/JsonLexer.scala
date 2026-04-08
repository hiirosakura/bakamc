package moe.forpleuvoir.nebula.serialization.json

import moe.forpleuvoir.nebula.serialization.ast.Token.*
import moe.forpleuvoir.nebula.serialization.ast.{Lexer, SyntaxReadException, Token, TokenPos}

import scala.annotation.tailrec

object JsonLexer extends Lexer {

  override def tokenize(input: String): List[Token] = {
    // 初始位置：行 1, 列 1, 偏移 0
    scan(input, 0, 1, 1, Nil)
  }

  @tailrec
  private def scan(input: String, cursor: Int, line: Int, col: Int, acc: List[Token]): List[Token] = {
    if (cursor >= input.length) return acc :+ EOF(TokenPos(line, col, cursor))

    val char = input.charAt(cursor)
    val currentPos = TokenPos(line, col, cursor)

    char match {
      // 1. 空白符处理：精确追踪行列
      case '\n' =>
        scan(input, cursor + 1, line + 1, 1, acc)
      case ' ' | '\t' | '\r' =>
        scan(input, cursor + 1, line, col + 1, acc)

      // 2. 结构化符号
      case '{' | '}' | '[' | ']' | ',' | ':' =>
        scan(input, cursor + 1, line, col + 1, acc :+ Token.Symbol(char.toString, currentPos))

      // 3. 字符串处理
      case '"' =>
        val (str, nextCursor) = readString(input, cursor, currentPos)
        // 虽然 JSON 字符串不跨行，但为了统一性，我们依然使用 calculatePos
        val (newLine, newCol) = calculatePos(input, cursor, nextCursor, line, col)
        scan(input, nextCursor, newLine, newCol, acc :+ Token.Literal(str, currentPos))

      // 4. 数字处理
      case c if c.isDigit || c == '-' =>
        val (numStr, nextCursor) = readNumber(input, cursor)
        val consumed = nextCursor - cursor
        scan(input, nextCursor, line, col + consumed, acc :+ Token.Literal(parseNumber(numStr), currentPos))

      // 5. 关键字 (true, false, null)
      case 't' | 'f' | 'n' =>
        val (word, nextCursor) = readWord(input, cursor)
        val consumed = nextCursor - cursor
        val token = word match {
          case "true"  => Token.Literal(true, currentPos)
          case "false" => Token.Literal(false, currentPos)
          case "null"  => Token.Literal(null, currentPos)
          case _       => throw SyntaxReadException(s"Invalid JSON identifier: $word", currentPos)
        }
        scan(input, nextCursor, line, col + consumed, acc :+ token)

      // 6. 错误处理
      case _ =>
        throw SyntaxReadException(s"Unexpected character: '$char'", currentPos)
    }
  }

  // --- 辅助工具方法 ---

  private def calculatePos(input: String, start: Int, end: Int, currLine: Int, currCol: Int): (Int, Int) = {
    var line = currLine
    var col = currCol
    var i = start
    while (i < end) {
      if (input(i) == '\n') {
        line += 1
        col = 1
      } else {
        col += 1
      }
      i += 1
    }
    (line, col)
  }

  private def readString(input: String, start: Int, startPos: TokenPos): (String, Int) = {
    val sb = new StringBuilder()
    var i = start + 1 // 跳过开头的双引号
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
            if (i + 4 < input.length) {
              val hex = input.substring(i + 1, i + 5)
              sb.append(Integer.parseInt(hex, 16).toChar)
              i += 4
            } else throw SyntaxReadException("Invalid unicode escape sequence", TokenPos(startPos.line, startPos.column + (i - start), i))
          case _ => throw SyntaxReadException(s"Invalid escape sequence: \\$c", TokenPos(startPos.line, startPos.column + (i - start), i))
        }
        escaped = false
      } else if (c == '\\') {
        escaped = true
      } else if (c == '"') {
        done = true
      } else if (c == '\n' || c == '\r') {
        // 标准 JSON 不允许字符串内换行
        throw SyntaxReadException("Unclosed string literal (JSON strings cannot contain raw newlines)", TokenPos(startPos.line, startPos.column + (i - start), i))
      } else {
        sb.append(c)
      }
      i += 1
    }
    if (!done) throw SyntaxReadException("Unclosed string literal", startPos)
    (sb.toString(), i)
  }

  private def readNumber(input: String, start: Int): (String, Int) = {
    var i = start
    while (i < input.length && "0123456789.+-eE".contains(input(i))) {
      i += 1
    }
    (input.substring(start, i), i)
  }

  private[serialization] def parseNumber(numStr: String): BigDecimal | Float | Double | BigInt | Int | Long = {
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