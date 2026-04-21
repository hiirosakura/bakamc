package cn.bakamc.folia.database.table

import cn.bakamc.folia.BakaMC
import cn.bakamc.folia.util.text.GSON
import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.{Item as MCItem, ItemStack as MCItemStack}
import org.bukkit.craftbukkit.CraftRegistry
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import slick.jdbc.H2Profile.Table
import slick.jdbc.MySQLProfile.api.*
import slick.lifted.{ProvenShape, Tag}

import java.nio.charset.StandardCharsets

case class SpecialItem(
  id: String,
  namespace: String,
  itemData: Array[Byte]
) {

  private[table] lazy val stackCache: Option[MCItemStack] = {
    try {
      val registry = CraftRegistry.getMinecraftRegistry
      val item = MCItemStack.SINGLE_ITEM_CODEC.decode(
        registry.createSerializationContext(JsonOps.COMPRESSED),
        JsonParser.parseString(String(itemData, StandardCharsets.UTF_8))
      ).result.get.getFirst
      Some(item)
    } catch {
      case e: Throwable =>
        BakaMC.logger.error("物品转换异常", e)
        None
    }
  }

}

object SpecialItem {

  given Conversion[MCItemStack, MCItem] = _.getItem

  def apply(id: String, nameSpace: String, itemData: Array[Byte]): SpecialItem = new SpecialItem(id, nameSpace, itemData)

  def apply(itemStack: ItemStack | MCItemStack): SpecialItem = {
    SpecialItem(getItemName(itemStack), itemStack)
  }

  def apply(id: String, itemStack: ItemStack | MCItemStack): SpecialItem = {
    val ops = CraftRegistry.getMinecraftRegistry.createSerializationContext(JsonOps.COMPRESSED)
    val stack = itemStack match {
      case item: MCItemStack => item
      case item: ItemStack => CraftItemStack.asNMSCopy(item)
    }
    val data = GSON.toJson(MCItemStack.SINGLE_ITEM_CODEC.encodeStart(ops, stack).result().get()).getBytes(StandardCharsets.UTF_8)
    SpecialItem(id, getItemNamespace(stack), data)
  }

  def getItemName(item: MCItemStack | ItemStack): String = {
    item match {
      case item: MCItemStack =>
        item.getItemName.getString
      case item: ItemStack =>
        CraftItemStack.asNMSCopy(item).getItemName.getString
    }
  }

  def getItemNamespace(item: MCItem | ItemStack): String = {
    item match {
      case item: MCItem =>
        BuiltInRegistries.ITEM.getKey(item).toString
      case item: ItemStack =>
        BuiltInRegistries.ITEM.getKey(CraftItemStack.asNMSCopy(item)).toString
    }
  }

  extension (self: SpecialItem) {

    def getVanillaItemStack(count: Int = 1): Option[MCItemStack] = {
      self.stackCache match {
        case Some(item) => Some(item.copyWithCount(count))
        case None => None
      }
    }

    def getItemStack(count: Int = 1): Option[ItemStack] = {
      getVanillaItemStack(count) match {
        case Some(item) => Some(CraftItemStack.asBukkitCopy(item))
        case None => None
      }
    }
  }
}

class SpecialItems(tag: Tag) extends Table[SpecialItem](tag, "special_items") {

  def id = column[String]("id", O.PrimaryKey, O.Length(36))

  def namespace = column[String]("namespace", O.Length(36))

  def itemData = column[Array[Byte]]("item_data")

  override def * : ProvenShape[SpecialItem] = (id, namespace, itemData).mapTo[SpecialItem]
}

val specialItems = TableQuery[SpecialItems]