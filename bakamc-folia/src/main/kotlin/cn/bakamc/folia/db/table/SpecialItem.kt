package cn.bakamc.folia.db.table

import cn.bakamc.folia.util.logger
import com.mojang.serialization.JsonOps
import moe.forpleuvoir.nebula.serialization.gson.parseToJsonElement
import moe.forpleuvoir.nebula.serialization.gson.toJsonStr
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.blob
import org.ktorm.schema.varchar

open class SpecialItems(alias: String?) : Table<SpecialItem>("special_item", alias) {
    companion object : SpecialItems(null)

    override fun aliased(alias: String): Table<SpecialItem> = SpecialItems(alias)

    val key = varchar("key").primaryKey().bindTo { it.key }

    val id = varchar("id").bindTo { it.id }

    val itemData = blob("item_data").bindTo { it.itemData }

}

interface SpecialItem : Entity<SpecialItem> {
    companion object : Entity.Factory<SpecialItem>()

    var key: String

    var id: String

    var itemData: ByteArray

}

fun ItemStack.toSpecialItem(key: String): SpecialItem {
    return SpecialItem {
        this.key = key
        this.id = nameSpace
        val result = ItemStack.SINGLE_ITEM_CODEC.encodeStart(JsonOps.COMPRESSED, this@toSpecialItem).result()
        this.itemData = ByteArray(0)
        result.ifPresent { this.itemData = it.toJsonStr().toByteArray(Charsets.UTF_8) }
    }
}

val Database.specialItems get() = this.sequenceOf(SpecialItems)

val ItemStack.nameSpace: String get() = BuiltInRegistries.ITEM.getKey(this.item).toString()

fun SpecialItem.toItemStack(count: Int): ItemStack? {
    runCatching {
        val item = ItemStack.SINGLE_ITEM_CODEC.decode(JsonOps.COMPRESSED, itemData.toString(Charsets.UTF_8).parseToJsonElement).result().get().first
        return item.apply {
            this.count = count
        }
    }.onFailure {
        logger.info("物品转换异常")
        it.printStackTrace()
    }
    return null
}

fun SpecialItem.isMatch(item: ItemStack): Boolean {
    runCatching {
        return ItemStack.isSameItemSameComponents(item, toItemStack(item.count)!!)
    }.onFailure {
        logger.info("物品匹配异常")
        it.printStackTrace()
    }
    return false
}

