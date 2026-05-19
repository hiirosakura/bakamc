package cn.bakamc.folia.util.matcher

import cn.bakamc.folia.config.MatcherConfig
import cn.bakamc.folia.util.matcher.base.MatchEntryMode.Include
import cn.bakamc.folia.util.matcher.base.{MatchEntry, MatchEntryMode}
import moe.forpleuvoir.nebula.serialization.base.{SerializeElement, SerializePrimitive}
import moe.forpleuvoir.nebula.serialization.codec.Codec
import org.bukkit.block.Block

import scala.util.Try


extension (value: Int) {
  private def inRange(a: Int, b: Int): Boolean = {
    val lower = math.min(a, b)
    val upper = math.max(a, b)
    value >= lower && value <= upper
  }
}

case class BlockMatcher(
  x: Option[(Int, Int)] = None,
  y: Option[(Int, Int)] = None,
  z: Option[(Int, Int)] = None,
  `type`: Option[String] = None,
  biome: Option[String] = None,
  world: Option[String] = None,
  worldType: Option[String] = None,
  override val mode: MatchEntryMode = MatchEntryMode.Include
) extends MatchEntry[Block] {

  override def test(target: Block): Boolean = {
    if (x.isEmpty && y.isEmpty && z.isEmpty && `type`.isEmpty && biome.isEmpty && world.isEmpty && worldType.isEmpty) return false
    `type`.forall(target.getBlockData.getMaterial.getKey.toString == _)
      && x.forall((a, b) => target.getX.inRange(a, b))
      && y.forall((a, b) => target.getY.inRange(a, b))
      && z.forall((a, b) => target.getZ.inRange(a, b))
      && biome.forall(target.getBiome.getKey.toString == _)
      && world.forall(target.getWorld.getName == _)
      && worldType.forall(target.getWorld.getKey.toString == _)
  }
}

object BlockMatcher {

  private val RangeCode = new Codec[(Int, Int)] {
    override def deserialization(data: SerializeElement): Try[(Int, Int)] = Try {
      data match {
        case SerializePrimitive(value: String) =>
          val parts = value.split("\\.\\.")
          if (parts.length != 2)
            throw new IllegalArgumentException(
              s"Expected format 'a..b', got '$value'"
            )
          parts(0).toInt -> parts(1).toInt
        case other =>
          throw new IllegalArgumentException(
            s"Expected SerializePrimitive with String, got $other"
          )
      }
    }

    override def serialization(value: (Int, Int)): SerializeElement =
      SerializePrimitive(s"${value._1}..${value._2}")
  }

  val CODEC: Codec[BlockMatcher] = Codec.create[BlockMatcher]
    .field("mode").getter(_.mode).default(Include).codec
    .field("x").getter(_.x).default(None).optionCodec(using RangeCode)
    .field("y").getter(_.y).default(None).optionCodec(using RangeCode)
    .field("z").getter(_.z).default(None).optionCodec(using RangeCode)
    .field("type").getter(_.`type`).default(None).optionCodec(using Codec.String)
    .field("biome").getter(_.biome).default(None).optionCodec(using Codec.String)
    .field("world").getter(_.world).default(None).optionCodec(using Codec.String)
    .field("world_type").getter(_.worldType).default(None).optionCodec(using Codec.String)
    .build((m, x, y, z, t, b, w, wt) => BlockMatcher(x, y, z, t, b, w, wt, m))

  given Codec[BlockMatcher] = CODEC

  def parse(input: String): BlockMatcher =
    MatcherConfig.blocks.get(input).getOrElse {
      parseFromString(input)
    }

  private def parseFromString(input: String): BlockMatcher = {
    val mode = if (input.startsWith("!")) MatchEntryMode.Exclude else MatchEntryMode.Include
    BlockMatcher(`type` = Some(input), mode = mode)
  }

}
