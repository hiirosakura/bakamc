package moe.forpleuvoir.nebula.serialization.ast

import moe.forpleuvoir.nebula.serialization.base.Primitive

sealed trait Token {
  def pos: TokenPos
}

case class TokenPos(line: Int, column: Int, offset: Int)

object Token {

  case class Symbol(value: String, pos: TokenPos) extends Token

  case class Literal(value: Primitive | Null, pos: TokenPos) extends Token

  case class Identifier(value: String, pos: TokenPos) extends Token

  case class EOF(pos: TokenPos) extends Token

  trait Special extends Token

}
