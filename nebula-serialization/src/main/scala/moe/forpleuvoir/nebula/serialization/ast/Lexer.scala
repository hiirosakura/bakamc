package moe.forpleuvoir.nebula.serialization.ast

trait Lexer {

  def tokenize(input: String): List[Token]

}
