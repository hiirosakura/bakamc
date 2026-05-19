package cn.bakamc.folia.util.matcher

import cn.bakamc.folia.config.MatcherConfig
import cn.bakamc.folia.util.matcher.base.{MatchEntry, MatchEntryMode}
import moe.forpleuvoir.nebula.serialization.codec.Codec
import org.bukkit.entity.Item

case class ItemMatcher(
  name: Option[String] = None,
  `type`: Option[String] = None,
  count: Option[Int] = None,
  override val mode: MatchEntryMode = MatchEntryMode.Include
) extends MatchEntry[Item] {

  override def test(target: Item): Boolean = {
    if (name.isEmpty && count.isEmpty && `type`.isEmpty) return false
    `type`.forall(target.getItemStack.getType.getKey.toString == _)
      && name.forall(target.getName == _)
      && count.forall(target.getItemStack.getAmount == _)
  }

}

object ItemMatcher {

  val CODEC: Codec[ItemMatcher] = Codec.create[ItemMatcher]
    .field("mode").getter(_.mode).codec
    .field("name").getter(_.name).optionCodec(using Codec.String)
    .field("type").getter(_.`type`).optionCodec(using Codec.String)
    .field("count").getter(_.count).optionCodec(using Codec.Int)
    .build((m, n, t, c) => ItemMatcher(n, t, c, m))

  given Codec[ItemMatcher] = CODEC

  def parse(input: String): ItemMatcher =
    MatcherConfig.items.get(input).getOrElse {
      parseFromString(input)
    }

  private def parseFromString(input: String): ItemMatcher = {
    val mode = if (input.startsWith("!")) MatchEntryMode.Exclude else MatchEntryMode.Include
    ItemMatcher(`type` = Some(input), mode = mode)
  }

}
