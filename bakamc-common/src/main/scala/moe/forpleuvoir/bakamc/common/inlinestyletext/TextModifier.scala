package moe.forpleuvoir.bakamc.common.inlinestyletext

import net.kyori.adventure.text.Component

trait TextModifier {

  def modify(exp: String): Option[Component => Component]

}
