package cn.bakamc.folia.item.customdata

import cn.bakamc.folia.util.matcher.MultiMatcher
import cn.bakamc.folia.util.matcher.SimpleMatcher
import com.google.common.cache.CacheBuilder
import net.minecraft.nbt.CompoundTag
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack

object InteractInterceptHandler {

    private const val KEY = "interact_intercept"

    private const val MAX_CACHE_SIZE = 500L

    private val matcherCache = CacheBuilder.newBuilder().maximumSize(MAX_CACHE_SIZE).build<String, MultiMatcher<String>>()

    private fun fromNbt(nbt: CompoundTag) = BakaData.fromNbt(nbt)?.getCompound(KEY)

    private fun fromItemStack(itemStack: ItemStack): CompoundTag? {
        return BakaData.fromItemStack(itemStack)?.getCompound(KEY)
    }

    fun handlerInteract(itemStack: ItemStack, type: InteractType): Boolean {
        fromItemStack(itemStack)?.apply {
            return if (contains(type.name)) {
                when (type) {
                    is InteractBlock  -> getMatcher(getString(type.name)).match(type.block?.blockData?.material?.key?.toString() ?: "minecraft:air")
                    is InteractEntity -> getMatcher(getString(type.name)).match(type.entity?.type?.key?.toString() ?: "minecraft:entity")
                }
            } else false
        }
        return false
    }

    private fun getMatcher(str: String): MultiMatcher<String> {
        return matcherCache.getIfPresent(str) ?: SimpleMatcher.parse(str).apply {
            matcherCache.put(str, this)
        }
    }

}

sealed class InteractType(val name: String)

sealed class InteractBlock(val block: Block?, name: String) : InteractType(name)

class RightClickBlock(block: Block?) : InteractBlock(block, "right_click_block")

class LeftClickBlock(block: Block?) : InteractBlock(block, "left_click_block")

sealed class InteractEntity(val entity: Entity?, name: String) : InteractType(name)

class RightClickEntity(entity: Entity?) : InteractEntity(entity, "right_click_entity")

class LeftClickEntity(entity: Entity?) : InteractEntity(entity, "left_click_entity")
