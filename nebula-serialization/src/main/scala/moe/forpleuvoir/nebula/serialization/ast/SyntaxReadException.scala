package moe.forpleuvoir.nebula.serialization.ast

case class SyntaxReadException(message: String, pos: TokenPos)
  extends RuntimeException(s"Error at [Line ${pos.line}, Col ${pos.column}]: $message")