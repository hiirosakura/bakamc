package cn.bakamc.folia.config

import cn.bakamc.folia.util.matcher.base.CompositeMatcherMode.Any
import cn.bakamc.folia.util.matcher.base.MatchEntryMode
import cn.bakamc.folia.util.matcher.*
import moe.forpleuvoir.nebula.config.CommentLines
import moe.forpleuvoir.nebula.config.item.ConfigMap

import scala.collection.immutable.ListMap

object MatcherConfig extends PluginConfigManager("matcher") {
  this.comment = List(
    "匹配器配置",
    "",
    "MatchEntry —— 单项匹配器",
    "定义单一的匹配规则。包含具体的匹配逻辑和匹配模式（MatchEntryMode）。",
    "",
    "MatchEntryMode —— 单项匹配器模式",
    "控制单项匹配结果的生效方式：",
    "  - Include：匹配通过时视为命中",
    "  - Exclude：匹配不通过时视为命中（即结果取反）",
    "",
    "CompositeMatcher —— 组合匹配器",
    "将多个 MatchEntry 聚合并以指定的组合模式（CompositeMatcherMode）判定最终结果。",
    "",
    "CompositeMatcherMode —— 组合匹配器模式",
    "控制多个 MatchEntry 之间的判定逻辑：",
    "  - Any：任意一个 MatchEntry 命中，则整体判定为匹配",
    "  - All：所有 MatchEntry 均命中，才判定为匹配",
    "  - None：所有 MatchEntry 均不命中，才判定为匹配"
  ).mkString("\r\n")

  @CommentLines(Array(
    "定义实体匹配器（CompositeMatcher[Entity]）",
    "格式：唯一名称 : 实体匹配器",
    "唯一名称: 作为索引供其他配置项引用",
    "",
    "实体匹配器 字段：",
    "  - mode: CompositeMatcherMode（Any / None / All）",
    "  - name:  MatchEntry，实体名称，为 null / 缺省时跳过",
    "  - uuid:  MatchEntry，实体 UUID，为 null / 缺省时跳过",
    "  - type:  MatchEntry，实体类型命名空间（如 minecraft:enderman），为 null / 缺省时跳过",
    "",
    "各 MatchEntry 的 MatchEntryMode 默认为 Include。",
    "CompositeMatcherMode 和 MatchEntryMode 详见 MatcherConfig 注释。",
    "",
    "默认匹配器说明：",
    "  小黑 - 匹配类型为 minecraft:enderman 的实体",
    "  不是小黑 - 排除类型不 minecraft:enderman 的实体",
    "  憨批 - 匹配名称为 dhwuia 的实体",
    "  不是憨批 - 排除名称为 dhwuia 的实体",
    "  forpleuvoir - 匹配指定 UUID 的实体",
    "  不是forpleuvoir - 排除指定 UUID 的实体",
    "  憨批小黑 - 匹配名称为 dhwuia 且类型为 minecraft:enderman 的实体",
    "  不是憨批的小黑 - 匹配名称不为 dhwuia 且类型为 minecraft:enderman 的实体",
    "  是憨批或者是小黑 - 匹配名称为 dhwuia 或者类型为 minecraft:enderman 的实体",
  ))
  val entities: ConfigMap[EntityMatcher] = ConfigMap("entities", ListMap(
    "小黑" -> EntityMatcher(entityType = Some(EntityTypeMatchEntry(`type` = "minecraft:enderman"))),
    "不是小黑" -> EntityMatcher(entityType = Some(EntityTypeMatchEntry(MatchEntryMode.Exclude, "minecraft:enderman"))),
    "憨批" -> EntityMatcher(name = Some(EntityNameMatchEntry(name = "dhwuia"))),
    "不是憨批" -> EntityMatcher(name = Some(EntityNameMatchEntry(MatchEntryMode.Exclude, "dhwuia"))),
    "forpleuvoir" -> EntityMatcher(uuid = Some(EntityUUIDMatchEntry(uuid = "808e8bd1-f808-4dec-aa46-ad64d3d6dc1c"))),
    "不是forpleuvoir" -> EntityMatcher(uuid = Some(EntityUUIDMatchEntry(MatchEntryMode.Exclude, "808e8bd1-f808-4dec-aa46-ad64d3d6dc1c"))),
    "憨批小黑" -> EntityMatcher(name = Some(EntityNameMatchEntry(name = "dhwuia")), entityType = Some(EntityTypeMatchEntry(`type` = "minecraft:enderman"))),
    "不是憨批的小黑" -> EntityMatcher(name = Some(EntityNameMatchEntry(MatchEntryMode.Exclude, "dhwuia")), entityType = Some(EntityTypeMatchEntry(`type` = "minecraft:enderman"))),
    "是憨批或者是小黑" -> EntityMatcher(name = Some(EntityNameMatchEntry(name = "dhwuia")), entityType = Some(EntityTypeMatchEntry(`type` = "minecraft:enderman")), mode = Any)
  ))


  @CommentLines(Array(
    "定义方块匹配器（MatchEntry[Block]）",
    "格式：唯一名称 : 方块匹配器",
    "唯一名称: 作为索引供其他配置项引用",
    "",
    "方块匹配器 字段：",
    "  - mode: MatchEntryMode（Include / Exclude），默认为 Include",
    "  - x:         坐标 X 轴范围，格式 \"a..b\"（含两端），为 null / 缺省时跳过",
    "  - y:         坐标 Y 轴范围，格式 \"a..b\"（含两端），为 null / 缺省时跳过",
    "  - z:         坐标 Z 轴范围，格式 \"a..b\"（含两端），为 null / 缺省时跳过",
    "  - type:      方块类型命名空间（如 minecraft:stone），为 null / 缺省时跳过",
    "  - biome:     群系命名空间（如 minecraft:plains），为 null / 缺省时跳过",
    "  - world:     世界名称（如 world），为 null / 缺省时跳过",
    "  - world_type: 世界类型命名空间（如 minecraft:overworld），为 null / 缺省时跳过",
    "",
    "BlockMatcher 的所有匹配条件之间为 AND 关系，需同时满足才视为匹配。",
    "MatchEntryMode 详见 MatcherConfig 注释。",
  ))
  val blocks: ConfigMap[BlockMatcher] = ConfigMap("blocks", ListMap(
    "西瓜" -> BlockMatcher(`type` = Some("minecraft:melon")),
    "主世界方块" -> BlockMatcher(worldType = Some("minecraft:overworld")),
    "下界方块" -> BlockMatcher(worldType = Some("minecraft:the_nether"))
  ))

  @CommentLines(Array(
    "定义物品匹配器（MatchEntry[Item]）",
    "格式：唯一名称 : 物品匹配器",
    "唯一名称: 作为索引供其他配置项引用",
    "",
    "物品匹配器 字段：",
    "  - mode: MatchEntryMode（Include / Exclude），默认为 Include",
    "  - type:  物品类型命名空间（如 minecraft:egg），为 null / 缺省时跳过",
    "  - name:  物品显示名称，为 null / 缺省时跳过",
    "  - count: 物品数量，为 null / 缺省时跳过",
    "",
    "ItemMatcher 的所有匹配条件之间为 AND 关系，需同时满足才视为匹配。",
    "MatchEntryMode 详见 MatcherConfig 注释。",
  ))
  val items: ConfigMap[ItemMatcher] = ConfigMap("items", ListMap(
    "鸡蛋" -> ItemMatcher(`type` = Some("minecraft:egg")),
    "一个鸡蛋" -> ItemMatcher(`type` = Some("minecraft:egg"), count = Some(1)),
  ))
}
