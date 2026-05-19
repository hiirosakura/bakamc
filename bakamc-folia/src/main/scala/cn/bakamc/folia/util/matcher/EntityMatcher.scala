package cn.bakamc.folia.util.matcher

import cn.bakamc.folia.config.MatcherConfig
import cn.bakamc.folia.util.matcher.base.{CompositeMatcher, CompositeMatcherMode, MatchEntry, MatchEntryMode}
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.Codec.{derived, given_Codec_String}
import org.bukkit.entity.Entity

case class EntityMatcher(
  name: Option[EntityNameMatchEntry] = None,
  uuid: Option[EntityUUIDMatchEntry] = None,
  entityType: Option[EntityTypeMatchEntry] = None,
  override val mode: CompositeMatcherMode = CompositeMatcherMode.All
) extends CompositeMatcher[Entity] {

  override lazy val entries: List[MatchEntry[Entity]] =
    List(name, uuid, entityType).flatten

  def isEmpty: Boolean = name.isEmpty && uuid.isEmpty && entityType.isEmpty

  override def test(target: Entity): Boolean = {
    if (entries.isEmpty) return false
    super.test(target)
  }

}

object EntityMatcher {

  val CODEC: Codec[EntityMatcher] = Codec.create[EntityMatcher]
    .field("mode").getter(_.mode).codec
    .field("name").getter(_.name).optionCodec(using EntityNameMatchEntry.derived$Codec)
    .field("uuid").getter(_.uuid).optionCodec(using EntityUUIDMatchEntry.derived$Codec)
    .field("type").getter(_.entityType).optionCodec(using EntityTypeMatchEntry.derived$Codec)
    .build((m, n, u, t) => EntityMatcher(n, u, t, m))

  given Codec[EntityMatcher] = CODEC


  def parse(input: String): EntityMatcher =
    MatcherConfig.entities.get(input).getOrElse {
      parseFromString(input)
    }

  private def parseFromString(input: String): EntityMatcher = {
    val mode = if (input.startsWith("!")) MatchEntryMode.Exclude else MatchEntryMode.Include
    EntityMatcher(entityType = Some(EntityTypeMatchEntry(mode, input)))
  }


}


case class EntityNameMatchEntry(
  override val mode: MatchEntryMode = MatchEntryMode.Include,
  name: String
) extends MatchEntry[Entity] derives Codec {
  override def test(target: Entity): Boolean = name == target.getName
}

case class EntityUUIDMatchEntry(
  override val mode: MatchEntryMode = MatchEntryMode.Include,
  uuid: String
) extends MatchEntry[Entity] derives Codec {
  override def test(target: Entity): Boolean = uuid == target.getUniqueId.toString
}

case class EntityTypeMatchEntry(
  override val mode: MatchEntryMode = MatchEntryMode.Include,
  `type`: String
) extends MatchEntry[Entity] derives Codec {
  override def test(target: Entity): Boolean = `type` == target.getType.getKey.toString
}

