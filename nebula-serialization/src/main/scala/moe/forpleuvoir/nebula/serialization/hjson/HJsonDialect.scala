package moe.forpleuvoir.nebula.serialization.hjson

import moe.forpleuvoir.nebula.serialization.ast.{SyntaxDialect, Token}
import moe.forpleuvoir.nebula.serialization.base.SerializeElement

import scala.util.Try

class HJsonDialect extends SyntaxDialect {

  override def tokenize(input: String): List[Token] = HJsonLexer.tokenize(input)

  override def decode(tokens: List[Token]): Try[SerializeElement] = HJsonDecoder.decode(tokens)

  override def encode(element: SerializeElement): String = HJsonEncoder.encode(element)
}


object HJsonDialect extends HJsonDialect {
}