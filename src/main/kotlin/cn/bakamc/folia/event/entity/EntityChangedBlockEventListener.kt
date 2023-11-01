package cn.bakamc.folia.event.entity

import cn.bakamc.folia.config.Configs.Entity.ChangeBlock.BLOCK_INFOS
import cn.bakamc.folia.config.Configs.Entity.ChangeBlock.ENTITY_MAP
import cn.bakamc.folia.config.Configs.Entity.ENTITY_INFOS
import cn.bakamc.folia.util.serialization
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.deserialization
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Blocks
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_20_R1.block.data.CraftBlockData
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.jetbrains.annotations.Contract

object EntityChangedBlockEventListener : Listener {

    fun reloadCache() {
        cache = cacheSupplier()
    }

    private var cache: Map<EntityInfo, List<BlockChange>> = cacheSupplier()

    private fun cacheSupplier() = ENTITY_MAP.mapKeys { (key, _) ->
        ENTITY_INFOS[key] ?: EntityInfo.EMPTY
    }.mapValues { (e, b) ->
        if (e != EntityInfo.EMPTY) b.map { BlockChange.parse(it) }
        else emptyList()
    }.filter { (key, _) ->
        key != EntityInfo.EMPTY
    }

    private inline fun inCache(entity: Entity, from: Block, to: String, action: () -> Unit) {
        cache.forEach { (e, b) ->
            if (e.isMatch(entity)) {
                b.find { it.isMatch(from, to) }?.let {
                    action()
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun entityChangedBlockEvent(event: EntityChangeBlockEvent) {
        println("${event.entity.name}[${event.block.blockData.material.key} -> ${event.to.key}]")

        var hasEntityMatched = false
        ENTITY_INFOS.values.forEach {
            if (it.isMatch(event.entity)) hasEntityMatched = true
        }
        var hasBlockMatched = false
        BLOCK_INFOS.values.forEach {
            if (it.isMatch(event.block)) hasBlockMatched = true
        }
        if (!hasEntityMatched || !hasBlockMatched) return

        inCache(event.entity, event.block, event.to.key.toString()) {
            event.isCancelled = true
            println("${event.entity.name}[${event.block.blockData.material.key} -> ${event.to.key}]事件被取消")
            return
        }
    }


}

data class EntityInfo(
    val name: String? = null,
    val uuid: String? = null,
    val type: String? = null,
) : Serializable {

    fun isMatch(target: Entity): Boolean {
        var result = false
        if (name != null) result = target.name == name
        if (uuid != null) result = result && target.uniqueId.toString() == uuid
        if (type != null) result = result && target.type.key.toString() == type
        return result
    }

    override fun serialization(): SerializeElement {
        return serializeObject {
            name?.let { "name" - it }
            uuid?.let { "uuid" - it }
            type?.let { "type" - it }
        }
    }

    companion object : Deserializer<EntityInfo> {

        val EMPTY = EntityInfo()

        override fun deserialization(serializeElement: SerializeElement): EntityInfo {
            serializeElement as SerializeObject
            return EntityInfo(
                serializeElement["name"]?.asString,
                serializeElement["uuid"]?.asString,
                serializeElement["type"]?.asString,
            )
        }
    }

}


data class BlockChange(
    val from: BlockInfo? = null,
    val to: BlockInfo? = null,
) {
    companion object {
        val EMPTY = BlockChange()

        const val symbol = "->"

        fun parse(string: String): BlockChange {
            if (string.contains(symbol)) {
                string.replace(" ", "").split(symbol).apply {
                    val from = if (this[0].isNotEmpty()) BLOCK_INFOS[this[0]] ?: BlockInfo(type = this[0]) else null
                    val to = if (this[1].isNotEmpty()) BLOCK_INFOS[this[1]] ?: BlockInfo(type = this[1]) else null
                    return BlockChange(from, to)
                }
            } else {
                return if (string.isNotEmpty()) BlockChange(BLOCK_INFOS[string] ?: BlockInfo(type = string)) else EMPTY
            }
        }
    }

    fun isMatch(from: Block, to: String): Boolean {
        var result = false
        if (this.from != null) result = this.from.isMatch(from)
        if (this.to != null) result = this.to.type == to
        return result
    }

}

data class BlockInfo(
    val x: IntRange? = null,
    val y: IntRange? = null,
    val z: IntRange? = null,
    val type: String? = null,
    val biome: String? = null,
    val world: String? = null,
) : Serializable {

    fun isMatch(block: Block): Boolean {
        TODO("判定方式待修复")
        var result = false
        if (x != null) result = block.x in x
        if (y != null) result = result && block.y in y
        if (z != null) result = result && block.z in z
        if (type != null) result = result && block.blockData.material.key.toString() == type
        if (biome != null) result = result && block.biome.key.toString() == biome
        if (world != null) result = result && block.world.key.toString() == world
        return result
    }

    override fun serialization(): SerializeElement {
        return serializeObject {
            x?.let { "x" - it.serialization() }
            y?.let { "y" - it.serialization() }
            z?.let { "z" - it.serialization() }
            type?.let { "type" - it }
            biome?.let { "biome" - it }
            world?.let { "world" - it }
        }
    }

    companion object : Deserializer<BlockInfo> {
        override fun deserialization(serializeElement: SerializeElement): BlockInfo {
            serializeElement as SerializeObject
            return BlockInfo(
                serializeElement["x"]?.let { IntRange.deserialization(it) },
                serializeElement["y"]?.let { IntRange.deserialization(it) },
                serializeElement["z"]?.let { IntRange.deserialization(it) },
                serializeElement["type"]?.asString,
                serializeElement["biome"]?.asString,
                serializeElement["world"]?.asString,
            )
        }

    }

}