package moe.forpleuvoir.nebula.serialization.ast

import moe.forpleuvoir.nebula.serialization.base.SerializeElement

import scala.util.Try

trait SyntaxDialect extends SyntaxEncoder, SyntaxDecoder, Lexer {

  def parse(input: String): Try[SerializeElement] = decode(tokenize(input))

}