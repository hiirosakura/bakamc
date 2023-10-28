package cn.bakamc.folia.db.table

import cn.bakamc.folia.util.logger
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.blob
import org.ktorm.schema.varchar
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

open class SpecialItems(alias: String?) : Table<SpecialItem>("special_item", alias) {
    companion object : SpecialItems(null)

    override fun aliased(alias: String): Table<SpecialItem> = SpecialItems(alias)

    val key = varchar("key").primaryKey().bindTo { it.key }

    val id = varchar("id").bindTo { it.id }

    val nbtTag = blob("nbt_tag").bindTo { it.nbtTag }

}

interface SpecialItem : Entity<SpecialItem> {
    companion object : Entity.Factory<SpecialItem>()

    var key: String

    var id: String

    var nbtTag: ByteArray

}

fun ItemStack.toSpecialItem(key: String): SpecialItem {
    return SpecialItem {
        this.key = key
        this.id = nameSpace
        this.nbtTag = tag?.let { writeNbtTag(it) } ?: ByteArray(0)
    }
}

val Database.specialItems get() = this.sequenceOf(SpecialItems)


val ItemStack.nameSpace: String get() = BuiltInRegistries.ITEM.getKey(this.item).toString()

fun readNbtTag(tagData: ByteArray): CompoundTag? {
    return if (tagData.isEmpty()) null
    else NbtIo.readCompressed(ByteArrayInputStream(tagData))

}

fun writeNbtTag(tag: CompoundTag?): ByteArray? {
    return if (tag == null) null
    else ByteArrayOutputStream(tag.sizeInBytes()).apply { NbtIo.writeCompressed(tag, this) }.toByteArray()
}

fun SpecialItem.toItemStack(count: Int): ItemStack? {
    runCatching {
        val item = BuiltInRegistries.ITEM.get(ResourceLocation(id))
        return ItemStack(item).apply {
            tag = readNbtTag(nbtTag)
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
        val idEquals = item.nameSpace == id
        return idEquals && readNbtTag(nbtTag) == item.tag
    }.onFailure {
        logger.info("物品匹配异常")
        it.printStackTrace()
    }
    return false
}

