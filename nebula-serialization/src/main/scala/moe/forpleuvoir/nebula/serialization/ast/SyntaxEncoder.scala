package moe.forpleuvoir.nebula.serialization.ast

import moe.forpleuvoir.nebula.serialization.base.SerializeElement

trait SyntaxEncoder {

  def encode(element: SerializeElement): String

}
