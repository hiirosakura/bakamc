package cn.bakamc.common.inlinestyletext

import net.kyori.adventure.text.Component

import scala.collection.mutable

private[inlinestyletext] object Decoder {

  def decode(tokens: List[Token], modifiers: ModifierContainer): Component = {
    val components = mutable.ListBuffer.empty[Component]
    // 存储当前文本段之前的所有表达式原始字符串
    val pendingExpressions = mutable.ListBuffer.empty[String]

    tokens.foreach {
      case Token.ControlStart(_) =>
        pendingExpressions.clear()

      case Token.Expression(raw, _) =>
        pendingExpressions += raw

      case Token.Literal(content, _) =>
        var current: Component = Component.text(content)
        pendingExpressions.foreach { exp =>
          modifiers.foreach { mod =>
            // 如果修改器匹配并返回了新的 Component，则更新 current
            mod.modify(exp, current).foreach { updated =>
              current = updated
            }
          }
        }
        components += current

      case _ =>
    }

    // 合并组件
    components.size match {
      case 0 => Component.empty()
      case 1 => components.head
      case _ =>
        import scala.jdk.CollectionConverters.*
        Component.text("").children(components.asJava)
    }
  }

}