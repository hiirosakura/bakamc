package cn.bakamc.folia.util.pojo

import cn.bakamc.folia.util.matcher.{EntityMatcher, ItemMatcher}
import org.bukkit.entity.{Entity, Item}

case class ItemPick(
  entity: Option[EntityMatcher],
  item: Option[ItemMatcher]
) {

  def test(entity: Entity, item: Item): Boolean =
    this.entity.forall(_.test(entity)) && this.item.forall(_.test(item))

}

object ItemPick {


}
