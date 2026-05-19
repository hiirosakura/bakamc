package cn.bakamc.folia.config

import cn.bakamc.folia.BakaMC
import cn.bakamc.folia.util.matcher.{BlockMatcher, EntityMatcher, ItemMatcher}
import cn.bakamc.folia.util.pojo.BlockChange
import moe.forpleuvoir.nebula.config.{Comment, CommentLines, ConfigGroup}
import moe.forpleuvoir.nebula.config.item.{ConfigBoolean, ConfigMap}
import moe.forpleuvoir.nebula.serialization.codec.Codec.given_Codec_String
import moe.forpleuvoir.nebula.serialization.codec.ListCodec

import scala.collection.immutable.ListMap
import language.implicitConversions

object EntityConfig extends ConfigGroup("entity") {

  private given ListCodec[String]

  @Comment("开启后将阻止实体修改方块,规则设置见 entity.change_block_map")
  private val interceptChangeBlock: ConfigBoolean = ConfigBoolean("intercept_change_block", false)

  @CommentLines(Array(
    "阻止实体更改方块的映射表",
    "key:   实体匹配器名称（引用 MatcherConfig.entities）或实体类型命名空间",
    "value: BlockChange 序列，每项格式为 \"源方块 -> 目标方块\"",
    "",
    "其中源方块和目标方块：",
    "  1. 优先从 MatcherConfig.blocks 中查找对应的 BlockMatcher",
    "  2. 未找到则将字符串直接作为方块类型的命名空间解析",
    "  3. 首字符为 '!' 时匹配模式为 Exclude",
    "  4. 目标方块实际上能变化的只有类型(方块命名空间),所以其他的匹配项都是无意义的",
    "",
    "示例：",
    "  \"小黑\" -> [\"主世界方块 -> minecraft:air\"]",
    "    表示匹配为 \"小黑\" 的实体不能将主世界方块搬起为空气",
  ))
  private val changeBlockMap: ConfigMap[List[String]] = ConfigMap("change_block_map", ListMap(
    "小黑" -> List("主世界方块 -> minecraft:air"),
  ))
  addChild(changeBlockMap)

  changeBlockMap.observe { config =>
    changeBlockVersion += 1
  }

  private var changeBlockVersion = 0
  private var changeBlockCacheVersion = -1
  private var _changeBlockCache: Map[EntityMatcher, List[BlockChange]] = Map.empty

  private def updateChangeBlockMap(): Unit = {
    val builder = ListMap.newBuilder[EntityMatcher, List[BlockChange]]
    changeBlockMap.map { case (k, v) =>
      val key = EntityMatcher.parse(k)
      val value = v.map(BlockChange.parse)
      if (!key.isEmpty) {
        builder.addOne(key -> value)
      }
    }
    _changeBlockCache = builder.result()
    BakaMC.logger.info(s"成功构建 阻止实体更改方块映射 缓存 ${_changeBlockCache.size}")
    changeBlockCacheVersion += 1
  }

  def changeBlockCache: Map[EntityMatcher, List[BlockChange]] =
    if (interceptChangeBlock) {
      if (changeBlockVersion != changeBlockCacheVersion) {
        updateChangeBlockMap()
      }
      _changeBlockCache
    } else Map.empty

  updateChangeBlockMap()

  @Comment("开启后将阻止实体爆炸破坏,规则设置见 entity.explode_block_map")
  private val interceptExplodeBlock: ConfigBoolean = ConfigBoolean("intercept_explode_block", false)

  @CommentLines(Array(
    "阻止实体爆炸破坏方块的映射表",
    "key:   实体匹配器名称（引用 MatcherConfig.entities）或实体类型命名空间",
    "value: 方块名称列表，该实体无法炸毁这些方块",
    "",
    "其中方块名称：",
    "  1. 优先从 MatcherConfig.blocks 中查找对应的 BlockMatcher",
    "  2. 未找到则将字符串直接作为方块类型的命名空间解析",
    "  3. 首字符为 '!' 时匹配模式为 Exclude",
    "",
    "示例：",
    "  \"minecraft:creeper\" -> [\"西瓜\", \"主世界方块\"]",
    "    表示苦力怕无法炸毁西瓜和主世界方块",
  ))
  private val explodeBlockMap: ConfigMap[List[String]] = ConfigMap("explode_block_map", ListMap(
    "minecraft:creeper" -> List("西瓜", "主世界方块")
  ))

  addChild(explodeBlockMap)

  private var explodeBlockVersion = 0

  explodeBlockMap.observe { config =>
    explodeBlockVersion += 1
  }

  private var explodeBlockCacheVersion = -1
  private var _explodeBlockCache: Map[EntityMatcher, List[BlockMatcher]] = Map.empty

  private def updateExplodeBlockCache(): Unit = {
    val builder = ListMap.newBuilder[EntityMatcher, List[BlockMatcher]]
    explodeBlockMap.map { case (k, v) =>
      val key = EntityMatcher.parse(k)
      val value = v.map(BlockMatcher.parse)
      if (!key.isEmpty) {
        builder.addOne(key -> value)
      }
    }
    _explodeBlockCache = builder.result()
    BakaMC.logger.info(s"成功构建 阻止实体爆炸破坏方块映射 缓存 ${_explodeBlockCache.size}")
    explodeBlockCacheVersion += 1
  }

  def explodeBlockCache: Map[EntityMatcher, List[BlockMatcher]] =
    if (interceptExplodeBlock) {
      if (explodeBlockCacheVersion != explodeBlockVersion) {
        updateExplodeBlockCache()
      }
      _explodeBlockCache
    } else Map.empty

  updateExplodeBlockCache()

  @Comment("开启后将阻止实体拾取物品,规则设置见 entity.pickup_item_map")
  private val interceptPickupItemMap: ConfigBoolean = ConfigBoolean("intercept_pickup_item", false)

  @CommentLines(Array(
    "阻止实体拾取物品的映射表",
    "key:   实体匹配器名称（引用 MatcherConfig.entities）或实体类型命名空间",
    "value: 物品名称列表，禁止该实体拾取这些物品",
    "",
    "其中物品名称：",
    "  1. 优先从对应配置中查找",
    "  2. 未找到则将字符串直接作为物品类型的命名空间解析",
    "  3. 首字符为 '!' 时匹配模式为 Exclude",
    "",
    "示例：",
    "  \"minecraft:zombie\" -> [\"鸡蛋\"]",
    "    表示僵尸不能拾取鸡蛋",
  ))
  private val pickupItemMap: ConfigMap[List[String]] = ConfigMap("pickup_item_map", ListMap(
    "minecraft:zombie" -> List("鸡蛋"),
    "minecraft:husk" -> List("一个鸡蛋"),
  ))

  addChild(pickupItemMap)

  pickupItemMap.observe { config =>
    pickupItemVersion += 1
  }

  private var pickupItemVersion = 0
  private var pickupItemCacheVersion = -1
  private var _pickupItemCache: Map[EntityMatcher, List[ItemMatcher]] = Map.empty

  private def updatePickupItemCache(): Unit = {
    val builder = ListMap.newBuilder[EntityMatcher, List[ItemMatcher]]
    pickupItemMap.map { case (k, v) =>
      val key = EntityMatcher.parse(k)
      val value = v.map(ItemMatcher.parse)
      if (!key.isEmpty) {
        builder.addOne(key -> value)
      }
    }
    _pickupItemCache = builder.result()
    BakaMC.logger.info(s"成功构建 阻止实体拾取物品映射 缓存 ${_pickupItemCache.size}")
    pickupItemCacheVersion += 1
  }

  def pickupItemCache: Map[EntityMatcher, List[ItemMatcher]] =
    if (interceptPickupItemMap) {
      if (pickupItemCacheVersion != pickupItemVersion) {
        updatePickupItemCache()
      }
      _pickupItemCache
    } else Map.empty

  updatePickupItemCache()

}
