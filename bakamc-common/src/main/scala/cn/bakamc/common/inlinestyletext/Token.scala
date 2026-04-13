package cn.bakamc.common.inlinestyletext

enum Token {
  case ControlStart(pos: Int)
  case ControlEnd(pos: Int)
  case Expression(raw: String, pos: Int)
  case Literal(raw: String, pos: Int)

  def pos: Int
}