package moe.forpleuvoir.nebula.serialization.ast

import moe.forpleuvoir.nebula.serialization.base.SerializeElement

import scala.util.Try

trait SyntaxDecoder {

  def decode(tokens: List[Token]): Try[SerializeElement]

}
