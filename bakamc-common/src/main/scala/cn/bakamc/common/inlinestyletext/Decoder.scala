package cn.bakamc.common.inlinestyletext

import net.kyori.adventure.text.Component

import scala.collection.mutable

object Decoder {

  def decode(tokens: List[Token], modifiers: List[TextModifier]): Component = {
    val components = mutable.ListBuffer.empty[Component]
    var activeTransforms = List.empty[Component => Component]
    tokens.foreach {
      case Token.ControlStart(_) => activeTransforms = List.empty
      case Token.Expression(raw, _) =>
        modifiers.foreach { mod =>
          mod.modify(raw).foreach(modifier =>
            activeTransforms = activeTransforms :+ modifier
          )
        }
      case Token.Literal(content, _) =>
        val initial: Component = Component.text(content)
        val processed = activeTransforms.foldLeft(initial) { (comp, transform) =>
          transform(comp)
        }
        components += processed
        activeTransforms = List.empty
      case _ =>
    }

    components.size match {
      case 0 => Component.empty()
      case 1 => components.head
      case _ =>
        import scala.jdk.CollectionConverters.*
        Component.empty().children(components.asJava)
    }
  }

}
