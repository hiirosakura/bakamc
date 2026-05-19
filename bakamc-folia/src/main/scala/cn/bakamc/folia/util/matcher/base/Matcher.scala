package cn.bakamc.folia.util.matcher.base

import moe.forpleuvoir.nebula.serialization.codec.Codec

trait Matcher[T] {

  def test(target: T): Boolean

}

trait MatchEntry[T] extends Matcher[T] {

  def mode: MatchEntryMode

  def testWithMode(target: T): Boolean = mode.handleResult(test(target))

}


enum MatchEntryMode derives Codec {
  case Include
  case Exclude

  def handleResult(result: Boolean): Boolean = this match {
    case Include => result
    case Exclude => !result
  }
}


trait CompositeMatcher[T] extends Matcher[T] {

  def entries: List[MatchEntry[T]]

  def mode: CompositeMatcherMode

  override def test(target: T): Boolean = mode match {
    case CompositeMatcherMode.Any => entries.exists(_.testWithMode(target))
    case CompositeMatcherMode.None => entries.forall(!_.testWithMode(target))
    case CompositeMatcherMode.All => entries.forall(_.testWithMode(target))
  }

}

enum CompositeMatcherMode derives Codec {
  /**
   * 在任意匹配模式下，只要存在一个匹配项符合条件，则整个匹配规则被视为通过。 该模式常用于需要满足至少一个条件即可的场景。
   */
  case Any
  /**
   * 当且仅当所有匹配项均不符合指定规则时，匹配才成功。 适用于需要确保没有一项符合条件的场景，例如检测某集合中是否不存在特定属性或值。
   */
  case None
  /**
   * 此模式通常用于需要确保全部条件均满足的场景。例如，当需要验证某个集合中所有元素都满足给定规则时，可采用此模式
   */
  case All
}