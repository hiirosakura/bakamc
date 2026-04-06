package moe.forpleuvoir.nebula.serialization.ast

import moe.forpleuvoir.nebula.serialization.base.Primitive

sealed trait Token

object Token {

  case class Symbol(value: String) extends Token

  case class Identifier(value: String) extends Token

  case class Literal(value: Primitive | Null) extends Token

  case object EOF extends Token

  trait Special extends Token

}
