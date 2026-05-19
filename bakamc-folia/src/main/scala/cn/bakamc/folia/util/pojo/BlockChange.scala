package cn.bakamc.folia.util.pojo

import cn.bakamc.folia.util.matcher.BlockMatcher
import org.bukkit.block.Block

case class BlockChange(
  from: Option[BlockMatcher],
  to: Option[BlockMatcher]
) {

  def test(from: Block, to: String): Boolean =
    this.from.forall(_.test(from)) && this.to.forall(_.`type`.exists(_ == to))

}

object BlockChange {

  private final val REGEX = "(.+) -> (.+)".r

  def parse(input: String): BlockChange = {
    input match {
      case REGEX(from, to) =>
        val f = Option(from).flatMap(s => Option(BlockMatcher.parse(s)))
        val t = Option(to).flatMap(s => Option(BlockMatcher.parse(s)))
        BlockChange(f, t)
      case _ => BlockChange(Some(BlockMatcher.parse(input)), None)
    }
  }

}