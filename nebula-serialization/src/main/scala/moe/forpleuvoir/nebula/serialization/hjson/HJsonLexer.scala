package moe.forpleuvoir.nebula.serialization.hjson

import moe.forpleuvoir.nebula.serialization.ast.Token.*
import moe.forpleuvoir.nebula.serialization.ast.{Lexer, Token, TokenPos}
import moe.forpleuvoir.nebula.serialization.json.JsonLexer

import scala.annotation.tailrec

object HJsonLexer extends Lexer {

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

      // 2. 注释处理：跳过后重置列号
      case '#' =>
        val nextCursor = skipLine(input, cursor)
        scan(input, nextCursor, line + 1, 1, acc)

      case '/' if isFollowedBy(input, cursor, '/') =>
        val nextCursor = skipLine(input, cursor)
        scan(input, nextCursor, line + 1, 1, acc)

      case '/' if isFollowedBy(input, cursor, '*') =>
        val nextCursor = skipBlockComment(input, cursor + 2)
        val (newLine, newCol) = calculatePos(input, cursor, nextCursor, line, col)
        scan(input, nextCursor, newLine, newCol, acc)

      // 3. 结构化符号
      case '{' | '}' | '[' | ']' | ',' | ':' =>
        scan(input, cursor + 1, line, col + 1, acc :+ Token.Symbol(char.toString, currentPos))

      // 4. 字符串处理 (处理单/双引号以及多行字符串)
      case '\'' if isTripleQuote(input, cursor) =>
        val (str, nextCursor) = readMultilineString(input, cursor + 3)
        val (newLine, newCol) = calculatePos(input, cursor, nextCursor, line, col)
        scan(input, nextCursor, newLine, newCol, acc :+ Token.Literal(str, currentPos))

      case '"' | '\'' =>
        val (str, nextCursor) = readQuotedString(input, cursor)
        val (newLine, newCol) = calculatePos(input, cursor, nextCursor, line, col)
        scan(input, nextCursor, newLine, newCol, acc :+ Token.Literal(str, currentPos))

      // 5. Unquoted (数字、布尔、Null 或普通文本)
      case c if isUnquotedCharStart(c) =>
        val (content, nextCursor) = readUnquoted(input, cursor)
        if (content.isEmpty) {
          scan(input, nextCursor, line, col + (nextCursor - cursor), acc)
        } else {
          val token = parseLiteralOrIdentifier(content, currentPos)
          scan(input, nextCursor, line, col + (nextCursor - cursor), acc :+ token)
        }

      // 6. 兜底逻辑
      case _ =>
        scan(input, cursor + 1, line, col + 1, acc)
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

  private def isTripleQuote(input: String, cursor: Int): Boolean =
    cursor + 2 < input.length && input(cursor) == '\'' && input(cursor + 1) == '\'' && input(cursor + 2) == '\''

  private def isFollowedBy(input: String, cursor: Int, target: Char): Boolean =
    cursor + 1 < input.length && input(cursor + 1) == target

  private def skipLine(input: String, cursor: Int): Int = {
    val nextLine = input.indexOf('\n', cursor)
    if (nextLine == -1) input.length else nextLine + 1
  }

  private def skipBlockComment(input: String, cursor: Int): Int = {
    val end = input.indexOf("*/", cursor)
    if (end == -1) input.length else end + 2
  }

  // --- 核心读取逻辑 ---

  private def readQuotedString(input: String, start: Int): (String, Int) = {
    val quote = input(start)
    val sb = new StringBuilder()
    var i = start + 1
    var escaped = false
    var done = false

    while (i < input.length && !done) {
      val c = input(i)
      if (escaped) {
        c match {
          case '"' | '\'' | '\\' | '/' => sb.append(c)
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
            } else throw new IllegalArgumentException(s"Invalid unicode escape at $i")
          case _ => sb.append(c)
        }
        escaped = false
      } else if (c == '\\') {
        escaped = true
      } else if (c == quote) {
        done = true
      } else {
        sb.append(c)
      }
      i += 1
    }
    if (!done) throw new IllegalArgumentException(s"Unclosed string at $start")
    (sb.toString(), i)
  }

  private def readMultilineString(input: String, start: Int): (String, Int) = {
    val end = input.indexOf("'''", start)
    val raw = input.substring(start, end)

    // 1. 获取所有行
    val lines = raw.replace("\r\n", "\n").split("\n", -1)

    // 2. 找到有效内容的起始行（跳过 ''' 后的首个空行）
    val contentLines = if (lines.nonEmpty && lines.head.trim.isEmpty) lines.tail else lines

    // 3. 确定基准：通常取第一行文字的缩进，或者所有行的最小公共缩进
    val baseIndent = contentLines.filter(_.trim.nonEmpty)
      .map(_.takeWhile(_ == ' ').length)
      .minOption.getOrElse(0)

    // 4. 裁剪每一行的基准缩进
    val finalContent = contentLines.map { line =>
      if (line.length >= baseIndent) line.substring(baseIndent) else line.stripLeading()
    }.mkString("\n").stripLineEnd

    (finalContent, end + 3)
  }

  private def readUnquoted(input: String, start: Int): (String, Int) = {
    var i = start
    var break = false
    while (i < input.length && !break) {
      val c = input(i)
      if (c == '\n' || c == '\r' || c == ',' || c == '}' || c == ']') break = true
      else if (c == ':') {
        if (i + 1 >= input.length || " \t\n\r".contains(input(i + 1))) break = true
        else i += 1
      }
      else if (c == '#' || (c == '/' && (isFollowedBy(input, i, '/') || isFollowedBy(input, i, '*')))) break = true
      else i += 1
    }
    (input.substring(start, i).trim, i)
  }

  private def isUnquotedCharStart(c: Char): Boolean =
    !c.isControl && !" \t\n\r{}[]:,#\"'".contains(c)

  private def parseLiteralOrIdentifier(content: String, pos: TokenPos): Token = {
    content match {
      case "true" => Token.Literal(true, pos)
      case "false" => Token.Literal(false, pos)
      case "null" => Token.Literal(null, pos)
      case s if s.matches("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?") =>
        try {
          Token.Literal(JsonLexer.parseNumber(s), pos)
        }
        catch {
          case _: Exception => Token.Identifier(s, pos)
        }
      case name => Token.Identifier(name, pos)
    }
  }
}
