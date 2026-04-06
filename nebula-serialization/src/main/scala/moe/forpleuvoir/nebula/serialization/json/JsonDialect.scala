package moe.forpleuvoir.nebula.serialization.json

import moe.forpleuvoir.nebula.serialization.ast.{SyntaxDialect, Token}
import moe.forpleuvoir.nebula.serialization.base.SerializeElement

import scala.util.Try

class JsonDialect(val useIndent: Boolean = true, val indentSize: Int = 2) extends SyntaxDialect {

  private val encoder = new JsonEncoder(useIndent, indentSize)

  override def tokenize(input: String): List[Token] = JsonLexer.tokenize(input)

  override def decode(tokens: List[Token]): Try[SerializeElement] = JsonDecoder.decode(tokens)

  override def encode(element: SerializeElement): String = encoder.encode(element)
}

object JsonDialect extends SyntaxDialect {

  private val Default: JsonDialect = new JsonDialect()

  override def tokenize(input: String): List[Token] = Default.tokenize(input)

  override def decode(tokens: List[Token]): Try[SerializeElement] = Default.decode(tokens)

  override def encode(element: SerializeElement): String = Default.encode(element)

  def apply(useIndent: Boolean, indentSize: Int): JsonDialect = new JsonDialect(useIndent, indentSize)

}